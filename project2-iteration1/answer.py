class answer(object):

    ##variables of answer class
    def __init__(self, pollname, questiontext, answertext):
        self.pollname = pollname
        self.questiontext = questiontext
        self.answertext = answertext

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



