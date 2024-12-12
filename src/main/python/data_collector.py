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

    answers = relationship("Answer", backref="thread", cascade="all, delete-orphan")  # 外键关系

class Answer(Base):
    __tablename__ = 'answers'

    id = Column(Integer, primary_key=True)
    thread_id = Column(Integer, ForeignKey('threads.id'))
    answer_id = Column(Integer, unique=True)  # 确保答案ID唯一
    body = Column(Text)
    score = Column(Integer)
    creation_date = Column(TIMESTAMP(timezone=True))  # 使用时区感知的时间

    comments = relationship("Comment", backref="answer", cascade="all, delete-orphan")  # 外键关系

class Comment(Base):
    __tablename__ = 'comments'

    id = Column(Integer, primary_key=True)
    answer_id = Column(Integer, ForeignKey('answers.id'))
    comment_id = Column(Integer, unique=True)  # 确保评论ID唯一
    body = Column(Text)
    creation_date = Column(TIMESTAMP(timezone=True))  # 使用时区感知的时间

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

# 数据存储函数
def save_threads_to_db(data):
    session = Session()
    if 'items' in data:
        for thread_data in data['items']:
            # 插入问题数据
            creation_date = datetime.utcfromtimestamp(thread_data['creation_date']).replace(tzinfo=utc)
            thread = Thread(
                question_id=thread_data['question_id'],
                title=thread_data['title'],
                body=thread_data.get('body', ''),
                tags=thread_data['tags'],
                creation_date=creation_date,
                score=thread_data['score']
            )
            session.add(thread)
            session.commit()

            # 获取答案
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
        answer = Answer(
            thread_id=thread.id,
            answer_id=answer_data['answer_id'],
            body=answer_data.get('body', ''),
            score=answer_data['score'],
            creation_date=creation_date
        )
        session.add(answer)
        session.commit()

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
    print(comments_data)

    for comment_data in comments_data['items']:
        creation_date = datetime.utcfromtimestamp(comment_data['creation_date']).replace(tzinfo=utc)
        comment = Comment(
            answer_id=answer.id,
            comment_id=comment_data['comment_id'],
            body=comment_data.get('body', ''),
            creation_date=creation_date
        )
        session.add(comment)
        session.commit()

# 主函数：抓取数据并存入数据库
def collect_data():
    page = 1
    while page <= 10:  # 假设抓取10页的数据
        print(f"抓取第 {page} 页的数据")
        data = fetch_threads(page=page)
        save_threads_to_db(data)
        page += 1
        time.sleep(1)  # 避免过快请求被限制

# 启动数据收集
collect_data()
