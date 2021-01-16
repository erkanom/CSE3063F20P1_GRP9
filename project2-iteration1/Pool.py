
class Pool(object):
    def __init__(self,poolName):
        self.poolName=poolName
        self.questionList=[]
        self.answerList=[]

    def addQuestion(self,question):
        self.questionList.append(question)

    def addAnswer(self,answer):
        self.answerList.append(answer)