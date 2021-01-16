
class Poll(object):
    def __init__(self,poolName,questionL,answerL,studentL,date):
        self.poolName=poolName
        self.questionList=questionL
        self.answerList=answerL
        self.studentAnswer=studentL
        self.date=date


    def addQuestion(self,question):
        self.questionList.append(question)

    def addAnswer(self,answer):
        self.answerList.append(answer)