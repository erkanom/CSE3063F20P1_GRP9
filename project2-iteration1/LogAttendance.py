
class LogAttendance(object):

    def __init__(self, studentName, studentEmail, submitDate, attQuestion, attAnswer):
        self.studentName = studentName
        self.studentEmail = studentEmail
        self.submitDate = submitDate
        self.attQuestion = attQuestion
        self.attAnswer = attAnswer

    def getStudentName(self):
        return self.studentName

    def setStudentName(self,studentName):
        self.studentName = studentName

    def getStudentEmail(self):
        return self.studentEmail

    def setStudentEmail(self,studentEmail):
        self.studentEmail = studentEmail

    def getSubmitDate(self):
        return self.submitDate

    def setSubmitDate(self, sumbitDate):
        self.submitDate = sumbitDate

    def getAttQuestion(self):
        return self.attQuestion

    def setAttQuestion(self, attQuestion):
        self.attQuestion = attQuestion

    def getAttAnswer(self):
        return self.attAnswer

    def setAttAnswer(self, attAnswer):
        self.attAnswer = attAnswer