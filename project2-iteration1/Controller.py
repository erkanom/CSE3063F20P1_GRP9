from CalculatorForPolls import CalculatorForPolls
from LogAttendance import LogAttendance
from Poll import Poll
import xlrd
import re
import os
import datetime
from Student import Student
from csv import reader
from Tr_Cap import tr_upper
from fuzzywuzzy import fuzz

from AnswerKey import AnswerKey


class Controller(object):
    def __init__(self):
        print("[LOG] Controller Object Created")
        self.studentList = []
        self.answerkeyList = []
        self.pollNameList = []
        self.allDates=[]

    # can be improved to getting extra arguments
    def readStudent(self):
        workbook = xlrd.open_workbook('CES3063_Fall2020_rptSinifListesi.xls')
        worksheet = workbook.sheet_by_name('rptSinifListesi')

        # For every row control column b  digit or something else
        for row in range(231):
            if isinstance(worksheet.cell(row, 1).value, float):
                self.studentList.append(
                    Student(worksheet.cell(row, 2).value, worksheet.cell(row, 4).value, worksheet.cell(row, 7).value,
                            worksheet.cell(row, 10).value))

    def readPools(self, name):
        stListCopy = self.studentList.copy()

        for t in stListCopy:
            if isinstance(t, Student):
                fullName = t.getStudentFullName()
                fullName = self.tChar(fullName)
                t.setStudentFullName(fullName)
                firstName = t.getStudentFirstName()
                firstName = self.tChar(firstName)
                t.setStudentFirstName(firstName)
                LastName = t.getStudentLastName()
                LastName = self.tChar(LastName)
                t.setStudentLastName(LastName)
        with open('./POLLS/' + name, 'r', encoding='utf-8') as po:
            # pass the file object to reader() to get the reader object
            olnolur = reader(po)
            duzeltme = reader(po)

            pollStList = []
            pollStList.clear()
            aa = olnolur.__next__()

            gir = 1
            count = 0

            for row in olnolur:
                dtxt = row[3]
                dtxt = dtxt.replace(",", "")
                dtxt = dtxt.split()
                datetime_object = datetime.datetime.strptime(dtxt[0], "%b")
                month_number = datetime_object.month
                date = datetime.datetime((int)(dtxt[2]), month_number, (int)(dtxt[1]))
                if self.allDates.count(date)==0:
                    self.allDates.append(date)
                if gir == 1:
                    rowText = row[4]
                    gir = 0
                question = []
                studentAnswer = []
                output = ""
                if row[4] != "Are you attending this lecture?" and row[4] != "":
                    studentPollName = row[1]
                    output = re.sub(r'\d+', '', studentPollName)
                    output = tr_upper(output)
                    output = self.tChar(output)

                    for x in range(4, len(row) - 1, 2):
                        question.append(row[x])
                        studentAnswer.append(row[x + 1])

                kackere = 0
                for listeElamani in pollStList:
                    beklenenIsim = listeElamani[0]
                    if output == beklenenIsim:
                        kackere = kackere + 1

                if kackere == 1:
                    gir = 1

                    if len(pollStList) > 0:
                        self.matchName(stListCopy, pollStList, date)
                        pollStList.clear()
                pollStList.append([output, question, studentAnswer])

            if len(pollStList) > 0:
                self.matchName(stListCopy, pollStList, date)
                pollStList.clear()
        a = 0

    def readAnswers(self, name):

        with open('ANSWERS/' + name, encoding='utf-8') as answerfile:

            # taking answer poll by poll

            gir = 1
            question = []
            answer = []
            olnolur = reader(answerfile)
            for row in olnolur:
                # getting first eleman as pollName
                if gir == 1:
                    pollName = row[0]
                    gir = 0
                    continue

                question.append(row[0])
                answer.append(row[1])
        answerObject = AnswerKey(pollName, question, answer)
        self.answerkeyList.append(answerObject)

    def readAttendance(self, name):
        stListCopy = self.studentList.copy()
        for t in stListCopy:
            if isinstance(t, Student):
                fullName = t.getStudentFullName()
                fullName = self.tChar(fullName)
                t.setStudentFullName(fullName)
                firstName = t.getStudentFirstName()
                firstName = self.tChar(firstName)
                t.setStudentFirstName(firstName)
                LastName = t.getStudentLastName()
                LastName = self.tChar(LastName)
                t.setStudentLastName(LastName)
        with open('./POLLS/' + name, 'r', encoding='utf-8') as attendance:
            # pass the file object to reader() to get the reader object
            attReader = reader(attendance)

            pollStList = []
            FirstRow = attReader.__next__()
            gir = 1
            count = 0
            for row in attReader:

                dtxt = row[3]
                dtxt = dtxt.replace(",", "")
                dtxt = dtxt.split()
                datetime_object = datetime.datetime.strptime(dtxt[0], "%b")
                month_number = datetime_object.month
                date = datetime.datetime((int)(dtxt[2]), month_number, (int)(dtxt[1]))
                if self.allDates.count(date)==0:
                    self.allDates.append(date)
                if row[4] == "Are you attending this lecture?":
                    studentPollName = row[1]
                    output = re.sub(r'\d+', '', studentPollName)
                    output = tr_upper(output)
                    output = self.tChar(output)
                    a = []
                    b = []
                    pollStList.append([output, a, b])

            self.matchName(stListCopy, pollStList, date)

    def startSystem(self):
        self.studentList.clear()
        self.readStudent()
        self.answerkeyList.clear()
        folder = "ANSWERS"
        for i in os.listdir(folder):
            file = os.path.join(folder, i)
            if os.path.isfile(file):
                self.readAnswers(i)

        folder = "POLLS"
        self.allDates.clear()
        for i in os.listdir(folder):
            file = os.path.join(folder, i)
            if os.path.isfile(file):
                self.readAttendance(i)
                self.readPools(i)
                pass



    def tChar(self, fullName):
        fullName = re.sub(r"İ", "I", fullName)
        fullName = re.sub(r"Ş", "S", fullName)
        fullName = re.sub(r"Ü", "U", fullName)
        fullName = re.sub(r"Ö", "O", fullName)
        fullName = re.sub(r"Ğ", "G", fullName)
        fullName = re.sub(r"Ç", "C", fullName)
        return fullName

    def matchName(self, stListCopy, pollStList, date):

        index = 1
        # 0 for attandance
        type = 0
        date = date
        tempList = pollStList.copy()
        pollNameAndAnswer = []
        if len(tempList) > 1:
            if len(tempList[1][1]) > 0:
                pollNameAndAnswer = self.whichPollMatch(tempList[1][1])
                type = 1
        Bys = stListCopy.copy()
        for studentInstance in Bys:
            if isinstance(studentInstance, Student):
                for data in tempList:
                    if isinstance(data, str):
                        name = data
                    else:
                        name = data[0]

                    if name == "HAMIORAK@SOMEMAIL.COM" and studentInstance.getStudentLastName() == "ORAK":
                        try:
                            for eleman in tempList:
                                isimStudent = eleman[0]
                                if isimStudent == name:
                                    tempList.remove(eleman)
                                    if type == 0:
                                        logAttandance = LogAttendance(date)
                                        studentInstance.getAttendanceLogs().append(logAttandance)
                                    else:
                                        poll = Poll(pollNameAndAnswer[0][0], data[1], pollNameAndAnswer[0][1], data[2],
                                                    date)
                                        studentInstance.getPollLog().append(poll)
                                    # print(index, studentInstance.getStudentFullName(), name)
                            ind = Bys.index(studentInstance)
                            Bys[ind] = None
                            index = index + 1
                        except:
                            a = 0
                    Ratio = fuzz.token_set_ratio(studentInstance.getStudentFullName(), name)
                    if Ratio == 100:
                        try:

                            for eleman in tempList:
                                isimStudent = eleman[0]
                                if isimStudent == name:
                                    tempList.remove(eleman)
                                    if type == 0:
                                        logAttandance = LogAttendance(date)
                                        studentInstance.getAttendanceLogs().append(logAttandance)
                                        # print(index, "ciktim", studentInstance.getStudentFullName(), name)

                                        ind = Bys.index(studentInstance)
                                        Bys[ind] = None
                                        index = index + 1
                                    else:
                                        poll = Poll(pollNameAndAnswer[0][0], data[1], pollNameAndAnswer[0][1], data[2],
                                                    date)
                                        studentInstance.getPollLog().append(poll)
                                        # print(index, "ciktim", studentInstance.getStudentFullName(), name)
                                        ind = Bys.index(studentInstance)
                                        Bys[ind] = None
                                        index = index + 1

                        except:
                            a = 0
                    elif Ratio >= 70:
                        # print(index, studentInstance.getStudentFullName(), name, Ratio)
                        last = name.split(" ")

                        RatioLN = fuzz.token_set_ratio(studentInstance.getStudentLastName(), last[len(last) - 1])
                        if RatioLN > 80:
                            # print(index, studentInstance.getStudentFullName(), name, RatioLN)
                            full = studentInstance.getStudentFullName().split(" ")
                            if RatioLN == 100:
                                if name == "AYSE KARAHASAN":
                                    try:
                                        for eleman in tempList:
                                            isimStudent = eleman[0]
                                            if isimStudent == name:
                                                tempList.remove(eleman)
                                                if type == 0:
                                                    logAttandance = LogAttendance(date)
                                                    studentInstance.getAttendanceLogs().append(logAttandance)
                                                    # print(index, studentInstance.getStudentFullName(), name)
                                                    ind = Bys.index(studentInstance)
                                                    Bys[ind] = None
                                                    index = index + 1
                                                else:
                                                    poll = Poll(pollNameAndAnswer[0][0], data[1],
                                                                pollNameAndAnswer[0][1], data[2], date)
                                                    studentInstance.getPollLog().append(poll)
                                                    # print(index, studentInstance.getStudentFullName(), name)
                                                    ind = Bys.index(studentInstance)
                                                    Bys[ind] = None
                                                    index = index + 1

                                    except:
                                        a = 0

                            if len(last) == 2 and len(full) == 2:

                                RatioFN = fuzz.token_set_ratio(studentInstance.getStudentFirstName(), last[0])
                                if RatioFN > 95:
                                    try:
                                        for eleman in tempList:
                                            isimStudent = eleman[0]
                                            if isimStudent == name:
                                                tempList.remove(eleman)
                                                if type == 0:
                                                    logAttandance = LogAttendance(date)
                                                    studentInstance.getAttendanceLogs().append(logAttandance)
                                                    # print(index, studentInstance.getStudentFullName(), name)
                                                    ind = Bys.index(studentInstance)
                                                    Bys[ind] = None
                                                    index = index + 1
                                                else:
                                                    poll = Poll(pollNameAndAnswer[0][0], data[1],
                                                                pollNameAndAnswer[0][1], data[2], date)
                                                    studentInstance.getPollLog().append(poll)
                                                    # print(index, studentInstance.getStudentFullName(), name)
                                                    ind = Bys.index(studentInstance)
                                                    Bys[ind] = None
                                                    index = index + 1

                                    except:
                                        a = 0
                            if len(last) == 2 and len(full) == 3:
                                InstanceFN = (str)(studentInstance.getStudentFirstName()).split(" ")
                                RatioFN1 = fuzz.token_set_ratio(InstanceFN[0], last[0])
                                RatioFN2 = fuzz.token_set_ratio(InstanceFN[1], last[0])
                                if (RatioFN1 + RatioFN2) / 2 > 50:
                                    try:
                                        for eleman in tempList:
                                            isimStudent = eleman[0]
                                            if isimStudent == name:
                                                tempList.remove(eleman)
                                                if type == 0:
                                                    logAttandance = LogAttendance(date)
                                                    studentInstance.getAttendanceLogs().append(logAttandance)
                                                    # print(index, studentInstance.getStudentFullName(), name)
                                                    ind = Bys.index(studentInstance)
                                                    Bys[ind] = None
                                                    index = index + 1
                                                else:
                                                    poll = Poll(pollNameAndAnswer[0][0], data[1],
                                                                pollNameAndAnswer[0][1], data[2], date)
                                                    studentInstance.getPollLog().append(poll)
                                                    # print(index, studentInstance.getStudentFullName(), name)
                                                    ind = Bys.index(studentInstance)
                                                    Bys[ind] = None
                                                    index = index + 1

                                    except:
                                        a = 0
                            if len(last) == 3 and len(full) == 3:
                                Ratiofn3Fuzz = fuzz.partial_ratio(studentInstance.getStudentFullName(), name)
                                Ratio3 = fuzz.token_set_ratio(studentInstance.getStudentFullName(), name)
                                if Ratio3 >= 80:
                                    try:
                                        for eleman in tempList:
                                            isimStudent = eleman[0]
                                            if isimStudent == name:
                                                tempList.remove(eleman)
                                                if type == 0:
                                                    logAttandance = LogAttendance(date)
                                                    studentInstance.getAttendanceLogs().append(logAttandance)
                                                    # print(index, studentInstance.getStudentFullName(), name)
                                                    ind = Bys.index(studentInstance)
                                                    Bys[ind] = None
                                                    index = index + 1
                                                else:
                                                    poll = Poll(pollNameAndAnswer[0][0], data[1],
                                                                pollNameAndAnswer[0][1], data[2], date)
                                                    studentInstance.getPollLog().append(poll)
                                                    # print(index, studentInstance.getStudentFullName(), name)
                                                    ind = Bys.index(studentInstance)
                                                    Bys[ind] = None
                                                    index = index + 1


                                    except:
                                        a = 0

    # TODO burda question listesine gore match yapip key icindeki name ile answerler donecek
    def whichPollMatch(self, questionList):
        result = []
        for key in self.answerkeyList:
            if isinstance(key, AnswerKey):
                correctQ = key.questiontext
                ratio = 0
                for index in range(len(correctQ)):
                    correctQuestion1 = correctQ[index]
                    asumptionQuestion = questionList[index]
                    R = fuzz.WRatio(correctQuestion1, asumptionQuestion)
                    ratio = ratio + R
                ratio = ratio / len(correctQ)

                if ratio > 90:
                    if self.pollNameList.count(key.pollname) == 0:
                        self.pollNameList.append(key.pollname)
                        result.append([key.pollname, key.answertext])
                    else:
                        many = self.pollNameList.count(key.pollname)
                        result.append([key.pollname + "-" + ((str)(many + 1)), key.answertext])
                        self.pollNameList.append(key.pollname + "-" + ((str)(many + 1)))

        return result

    def calculateOutput(self,students,dates):
        calculator=CalculatorForPolls(students,dates)
        calculator.startCalculations()
pass
