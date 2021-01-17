class AnswerKey(object):

    ##variables of answer class
    def __init__(self, pollname, questions, answers):
        self.pollname = pollname
        self.questiontext = questions
        self.answertext = answers

    def getpollname(self):
        return self.pollname

    def setpollname(self,pollname):
        self.pollname = pollname

    def getquestiontext(self):
        return self.questiontext

    def setquestiontext(self,questiontext):
        self.questiontext = questiontext

    def getanswertext(self):
        return self.answertext

    def setanswertext(self, answertext):
        self.answertext = answertext



