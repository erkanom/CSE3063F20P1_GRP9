
class Pool(object):
    def __init__(self,poolName,questionL,answerL,studentL):
        self.poolName=poolName
        self.questionList=questionL
        self.answerList=answerL
        self.studentAnswer=studentL


    def addQuestion(self,question):
        self.questionList.append(question)

    def addAnswer(self,answer):
        self.answerList.append(answer)