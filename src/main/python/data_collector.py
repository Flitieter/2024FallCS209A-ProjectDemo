import requests
import time
from sqlalchemy import create_engine, Column, Integer, String, Text, ForeignKey, TIMESTAMP, ARRAY
from sqlalchemy.orm import sessionmaker, relationship
from sqlalchemy.ext.declarative import declarative_base
from datetime import datetime
import pytz

# 设置数据库连接
DATABASE_URL = "postgresql://postgres:200516@localhost/stkovflw"
engine = create_engine(DATABASE_URL)
Session = sessionmaker(bind=engine)
Base = declarative_base()

# 设置时区为 UTC
utc = pytz.UTC

# 定义数据库表结构
class Thread(Base):
    __tablename__ = 'threads'

    id = Column(Integer, primary_key=True)
    question_id = Column(Integer, unique=True)  # 确保问题ID唯一
    title = Column(Text)
    body = Column(Text)
    tags = Column(ARRAY(String))
    creation_date = Column(TIMESTAMP(timezone=True))  # 使用时区感知的时间
    score = Column(Integer)
    owner_id = Column(Integer)  # 新增：拥有者的用户ID
    owner_reputation = Column(Integer)  # 新增：拥有者的声誉

    answers = relationship("Answer", backref="thread", cascade="all, delete-orphan")  # 外键关系

class Answer(Base):
    __tablename__ = 'answers'

    id = Column(Integer, primary_key=True)
    thread_id = Column(Integer, ForeignKey('threads.id'))
    answer_id = Column(Integer, unique=True)  # 确保答案ID唯一
    body = Column(Text)
    score = Column(Integer)
    creation_date = Column(TIMESTAMP(timezone=True))  # 使用时区感知的时间
    owner_id = Column(Integer)  # 拥有者的用户ID
    owner_reputation = Column(Integer)  # 拥有者的声誉
    is_accepted = Column(Integer)  # 新增：是否被接受（0 或 1）

    comments = relationship("Comment", backref="answer", cascade="all, delete-orphan")  # 外键关系

class Comment(Base):
    __tablename__ = 'comments'

    id = Column(Integer, primary_key=True)
    answer_id = Column(Integer, ForeignKey('answers.id'))
    comment_id = Column(Integer, unique=True)  # 确保评论ID唯一
    body = Column(Text)
    creation_date = Column(TIMESTAMP(timezone=True))  # 使用时区感知的时间
    owner_id = Column(Integer)  # 新增：拥有者的用户ID
    owner_reputation = Column(Integer)  # 新增：拥有者的声誉

# 创建所有表
Base.metadata.create_all(engine)

# API抓取函数
def fetch_threads(tag='java', page=1, pagesize=100):
    url = f"https://api.stackexchange.com/2.3/questions"
    params = {
        'tagged': tag,
        'page': page,
        'pagesize': pagesize,
        'site': 'stackoverflow',
        'filter': 'withbody',
        'key': 'rl_NHBzEeTBPzT8FkF6DgbZQhVUD'
    }
    response = requests.get(url, params=params)
    return response.json()

# 获取并保存问题数据
def save_threads_to_db(data):
    session = Session()
    if 'items' in data:
        for thread_data in data['items']:
            # 插入问题数据
            creation_date = datetime.utcfromtimestamp(thread_data['creation_date']).replace(tzinfo=utc)

            # 使用 get 方法安全地提取 owner_id 和 owner_reputation
            owner = thread_data.get('owner', {})
            owner_id = owner.get('user_id') if owner else None
            owner_reputation = owner.get('reputation') if owner else None

            thread = Thread(
                question_id=thread_data['question_id'],
                title=thread_data['title'],
                body=thread_data.get('body', ''),
                tags=thread_data['tags'],
                creation_date=creation_date,
                score=thread_data['score'],
                owner_id=owner_id,
                owner_reputation=owner_reputation
            )

            try:
                session.add(thread)
                session.commit()
            except Exception as e:
                print(f"Error while adding thread {thread_data['question_id']} to the database: {e}")
                session.rollback()  # 回滚事务
                continue  # 跳过当前线程，继续处理下一个

            # 获取答案并保存
            fetch_answers_and_save(thread_data['question_id'], thread, session)
    else:
        print("返回的数据没有 'items' 键，请检查 API 响应")
    session.close()

# 获取并保存答案
def fetch_answers_and_save(question_id, thread, session):
    url = f"https://api.stackexchange.com/2.3/questions/{question_id}/answers"
    params = {
        'site': 'stackoverflow',
        'filter': 'withbody',
        'key': 'rl_NHBzEeTBPzT8FkF6DgbZQhVUD'
    }

    response = requests.get(url, params=params)
    answers_data = response.json()

    for answer_data in answers_data['items']:
        creation_date = datetime.utcfromtimestamp(answer_data['creation_date']).replace(tzinfo=utc)

        # 安全地获取 owner_id 和 owner_reputation
        owner = answer_data.get('owner', {})
        owner_id = owner.get('user_id') if owner else None
        owner_reputation = owner.get('reputation') if owner else None

        # 获取是否被接受
        is_accepted = answer_data.get('is_accepted', False)  # 默认值为 False

        answer = Answer(
            thread_id=thread.id,
            answer_id=answer_data['answer_id'],
            body=answer_data.get('body', ''),
            score=answer_data['score'],
            creation_date=creation_date,
            owner_id=owner_id,
            owner_reputation=owner_reputation,
            is_accepted=1 if is_accepted else 0  # 将布尔值转换为 1 或 0
        )
        try:
            session.add(answer)
            session.commit()
        except Exception as e:
            print(f"Error while adding answer {answer_data['answer_id']} to the database: {e}")
            session.rollback()  # 回滚事务
            continue  # 跳过当前答案，继续处理下一个

        # 获取并保存评论
        fetch_comments_and_save(answer_data['answer_id'], answer, session)


# 获取并保存评论
def fetch_comments_and_save(answer_id, answer, session):
    url = f"https://api.stackexchange.com/2.3/answers/{answer_id}/comments"
    params = {
        'site': 'stackoverflow',
        'key': 'rl_NHBzEeTBPzT8FkF6DgbZQhVUD'
    }
    response = requests.get(url, params=params)
    comments_data = response.json()

    for comment_data in comments_data['items']:
        creation_date = datetime.utcfromtimestamp(comment_data['creation_date']).replace(tzinfo=utc)

        # 安全地获取 owner_id 和 owner_reputation
        owner = comment_data.get('owner', {})
        owner_id = owner.get('user_id') if owner else None
        owner_reputation = owner.get('reputation') if owner else None

        comment = Comment(
            answer_id=answer.id,
            comment_id=comment_data['comment_id'],
            body=comment_data.get('body', ''),
            creation_date=creation_date,
            owner_id=owner_id,
            owner_reputation=owner_reputation
        )
        try:
            session.add(comment)
            session.commit()
        except Exception as e:
            print(f"Error while adding comment {comment_data['comment_id']} to the database: {e}")
            session.rollback()  # 回滚事务
            continue  # 跳过当前评论，继续处理下一个


# 主函数：抓取数据并存入数据库
def collect_data():
    page = 11
    while page <= 11:  # 假设抓取10页的数据
        print(f"抓取第 {page} 页的数据")
        data = fetch_threads(page=page)
        save_threads_to_db(data)
        page += 1
        time.sleep(1)  # 避免过快请求被限制

# 启动数据收集
collect_data()
