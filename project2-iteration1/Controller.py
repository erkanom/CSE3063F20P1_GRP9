import xlrd
import re
import datetime
from Student import Student
from csv import reader
from Tr_Cap import tr_upper
from fuzzywuzzy import fuzz
from answer import answer


class Controller(object):
    def __init__(self):
        print("[LOG] Controller Object Created")
        self.studentList = []
        self.poolList = []
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

    def readPools(self):
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
        with open('./POOLS/CSE3063_20201123_Mon_zoom_PollReport.csv', 'r', encoding='utf-8') as attendance:
            # pass the file object to reader() to get the reader object
            attReader = reader(attendance)

            pollStList = []
            for row in attReader:
                question=[]
                studentAnswer=[]
                if row[4] != "Are you attending this lecture?" and row[4] != "":
                    studentPollName = row[1]
                    output = re.sub(r'\d+', '', studentPollName)
                    output = tr_upper(output)
                    output = self.tChar(output)
                    pollStList.append(output)
                    for x in range(4,len(row)-1):
                        question.append(row[x])
                        studentAnswer.append(row[x+1])
                        x=x+2
                    pollStList.append([output,question,answer])
            self.matchName(stListCopy, pollStList)

    def readAnswers(self):

        with open('answerkey.csv', 'r') as answerfile:

            # taking answer poll by poll
            answer_read = answerfile.readlines()
            for row in answer_read:
                variables = row.split(";")

                if (variables[0].find("CSE3063") != -1):
                    self.poolname.append(variables[0])
                    if (len(self.poolname) > 1):
                        del self.poolname[0]
                else:
                    self.answerkeyList.append(answer(self.poolname[0], variables[0], variables[1]))

    def readAttendance(self):
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
        with open('./POOLS/CSE3063_20201123_Mon_zoom_PollReport.csv', 'r', encoding='utf-8') as attendance:
            # pass the file object to reader() to get the reader object
            attReader = reader(attendance)

            pollStList = []
            for row in attReader:
                if row[4] == "Are you attending this lecture?":
                    studentPollName = row[1]
                    output = re.sub(r'\d+', '', studentPollName)
                    output = tr_upper(output)
                    output = self.tChar(output)
                    pollStList.append(output)


            self.matchName(stListCopy, pollStList)

    def startSystem(self):
        self.studentList.clear()
        self.poolList.clear()
        self.readStudent()
        self.readAttendance()
        self.readPools()
        self.readAnswers()

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
        tempList = stListCopy.copy()
        for studentInstance in stListCopy:
            if isinstance(studentInstance, Student):
                for data in pollStList:
                    if isinstance(data, str):
                        name = data
                    else:
                        name = data[0]
                    Ratio = fuzz.token_set_ratio(studentInstance.getStudentFullName(), name)
                    if Ratio > 70:
                        # print(index, studentInstance.getStudentFullName(), name, Ratio)
                        last = name.split(" ")
                        RatioLN = fuzz.token_sort_ratio(studentInstance.getStudentLastName(), last[len(last) - 1])
                        if RatioLN > 80:
                            # print(index, studentInstance.getStudentFullName(), name, RatioLN)
                            full = studentInstance.getStudentFullName().split(" ")
                            if len(last) == 2 and len(full) == 2:
                                RatioFN = fuzz.token_set_ratio(studentInstance.getStudentFirstName(), last[0])
                                if RatioFN > 55:
                                    try:
                                        tempList.remove(studentInstance)
                                        print(index, studentInstance.getStudentFullName(), name)
                                        index = index + 1
                                    except:
                                        print("instance removed before")
                            if len(last) == 2 and len(full) == 3:
                                InstanceFN = (str)(studentInstance.getStudentFirstName()).split(" ")
                                RatioFN1 = fuzz.token_set_ratio(InstanceFN[0], last[0])
                                RatioFN2 = fuzz.token_set_ratio(InstanceFN[1], last[0])
                                if (RatioFN1 + RatioFN2) / 2 >= 50:
                                    try:
                                        tempList.remove(studentInstance)
                                        print(index, studentInstance.getStudentFullName(), name)
                                        index = index + 1
                                    except:
                                        print("instance removed before")
                            if len(last) == 3 and len(full) == 3:
                                Ratio3 = fuzz.token_set_ratio(studentInstance.getStudentFullName(), name)
                                if Ratio3 > 80:
                                    try:
                                        tempList.remove(studentInstance)
                                        print(index, studentInstance.getStudentFullName(), name)
                                        index = index + 1
                                    except:
                                        print("instance removed before")
        pass
