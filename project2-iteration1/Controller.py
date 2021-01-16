import xlrd
import re
import os
import datetime
from Student import Student
from csv import reader
from Tr_Cap import tr_upper
from fuzzywuzzy import fuzz
from fuzzywuzzy import process
from answer import answer


class Controller(object):
    def __init__(self):
        print("[LOG] Controller Object Created")
        self.studentList = []

        self.answerkeyList = []
        self.poolname = []

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
        with open('./POOLS/' + name, 'r', encoding='utf-8') as po:
            # pass the file object to reader() to get the reader object
            olnolur = reader(po)
            duzeltme = reader(po)

            pollStList = []
            pollStList.clear()
            aa = olnolur.__next__()

            gir = 1
            count = 0
            index = 0
            for row in olnolur:

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

                    for x in range(4, len(row) - 1):
                        question.append(row[x])
                        studentAnswer.append(row[x + 1])
                        x = x + 2
                kackere = 0
                for listeElamani in pollStList:
                    beklenenIsim = listeElamani[0]
                    if output == beklenenIsim:
                        kackere = kackere + 1

                if kackere == 1:
                    gir = 1

                    if len(pollStList) > 0:
                        self.matchName(stListCopy, pollStList)
                        pollStList.clear()
                pollStList.append([output, question, answer])
                index = index + 1
            if len(pollStList) > 0:
                self.matchName(stListCopy, pollStList)
        a = 0

    def readAnswers(self):

        with open('answerkey.csv', 'r') as answerfile:

            # taking answer poll by poll
            answer_read = answerfile.readlines()
            for row in answer_read:
                # a;b   a;b;c;d     e
                variables = row.split(";")

                if (variables[0].find("CSE3063") != -1):
                    self.poolname.append(variables[0])
                    if (len(self.poolname) > 1):
                        del self.poolname[0]
                else:
                    self.answerkeyList.append(answer(self.poolname[0], variables[0], variables[1]))

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
        with open('./POOLS/' + name, 'r', encoding='utf-8') as attendance:
            # pass the file object to reader() to get the reader object
            attReader = reader(attendance)

            pollStList = []
            FirstRow = attReader.__next__()
            gir = 1
            count = 0
            for row in attReader:

                if row[4] == "Are you attending this lecture?":
                    studentPollName = row[1]
                    output = re.sub(r'\d+', '', studentPollName)
                    output = tr_upper(output)
                    output = self.tChar(output)
                    a=[]
                    b=[]
                    pollStList.append([output, a, b])





            self.matchName(stListCopy, pollStList)

    def startSystem(self):
        self.studentList.clear()
        self.readStudent()
        folder = "POOLS"
        for i in os.listdir(folder):
            file = os.path.join(folder, i)
            if os.path.isfile(file):
                self.readAttendance(i)
                self.readPools(i)

        # self.readAnswers()

    def tChar(self, fullName):
        fullName = re.sub(r"İ", "I", fullName)
        fullName = re.sub(r"Ş", "S", fullName)
        fullName = re.sub(r"Ü", "U", fullName)
        fullName = re.sub(r"Ö", "O", fullName)
        fullName = re.sub(r"Ğ", "G", fullName)
        fullName = re.sub(r"Ç", "C", fullName)
        return fullName

    def matchName(self, stListCopy, pollStList):
        index = 1
        tempList = pollStList.copy()
        Bys = stListCopy.copy()
        for studentInstance in Bys:
            if isinstance(studentInstance, Student):
                for data in tempList:
                    if isinstance(data, str):
                        name = data
                    else:
                        name = data[0]

                    if name == "HAMIORAK@SOMEMAIL.COM" and studentInstance.getStudentLastName()=="ORAK":
                        try:
                            for eleman in tempList:
                                isimStudent = eleman[0]
                                if isimStudent == name:
                                    tempList.remove(eleman)
                                    print(index, studentInstance.getStudentFullName(), name)
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
                            print(index,"ciktim", studentInstance.getStudentFullName(), name)
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
                                        print(index, studentInstance.getStudentFullName(), name)
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
                                        print(index, studentInstance.getStudentFullName(), name)
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
                                        print(index, studentInstance.getStudentFullName(), name)
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
                                        print(index, studentInstance.getStudentFullName(), name)
                                        ind = Bys.index(studentInstance)
                                        Bys[ind] = None
                                        index = index + 1
                                    except:
                                        a = 0




pass
