
class Poll(object):
    def __init__(self,poolName,questionL,answerL,studentL,date):
        self.poolName=poolName
        self.questionList=questionL
        self.answerList=answerL
        self.studentAnswer=studentL
        self.date=date




    def getDate(self):
        return self.date

    def getName(self):
        return self.poolName

    def getQuestion(self):
        return self.questionList

    def addQuestion(self,question):
        self.questionList.append(question)

    def addAnswer(self,answer):
        self.answerList.append(answer)

    def getstudentAnswer(self):
        return self.studentAnswer

    def getanswerList(self):
        return self.answerList