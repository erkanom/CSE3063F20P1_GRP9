import xlrd
import re
import datetime
from Student import Student
from csv import reader
from Tr_Cap import tr_upper
from fuzzywuzzy import fuzz


class Controller(object):
    def __init__(self):
        print("[LOG] Controller Object Created")
        self.studentList = []
        self.poolList = []

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
        pass

    def readAttendance(self):
        stListCopy = self.studentList.copy()

        for t in stListCopy:
            if isinstance(t, Student):
                fullName = t.getStudentFullName()
                fullName = re.sub(r"İ", "I", fullName)
                fullName = re.sub(r"Ş", "S", fullName)
                fullName = re.sub(r"Ü", "U", fullName)
                fullName = re.sub(r"Ö", "O", fullName)
                fullName = re.sub(r"Ğ", "G", fullName)
                fullName = re.sub(r"Ç", "C", fullName)
                t.setStudentFullName(fullName)
                firstName = t.getStudentFirstName()
                firstName = re.sub(r"İ", "I", firstName)
                firstName = re.sub(r"Ş", "S", firstName)
                firstName = re.sub(r"Ü", "U", firstName)
                firstName = re.sub(r"Ö", "O", firstName)
                firstName = re.sub(r"Ğ", "G", firstName)
                firstName = re.sub(r"Ç", "C", firstName)
                t.setStudentFirstName(firstName)
                LastName = t.getStudentLastName()
                LastName = re.sub(r"İ", "I", LastName)
                LastName = re.sub(r"Ş", "S", LastName)
                LastName = re.sub(r"Ü", "U", LastName)
                LastName = re.sub(r"Ö", "O", LastName)
                LastName = re.sub(r"Ğ", "G", LastName)
                LastName = re.sub(r"Ç", "C", LastName)
                t.setStudentLastName(LastName)

        with open('./POOLS/CSE3063_20201124_Tue_zoom_PollReport.csv', 'r', encoding='utf-8') as attendance:
            # pass the file object to reader() to get the reader object
            attReader = reader(attendance)
            index = 1
            pollStList = []

            for row in attReader:

                if row[4] == "Are you attending this lecture?":
                    studentPollName = row[1]
                    output = re.sub(r'\d+', '', studentPollName)
                    output = tr_upper(output)
                    output = re.sub(r"İ", "I", output)
                    output = re.sub(r"Ş", "S", output)
                    output = re.sub(r"Ü", "U", output)
                    output = re.sub(r"Ö", "O", output)
                    output = re.sub(r"Ğ", "G", output)
                    output = re.sub(r"Ç", "C", output)
                    pollStList.append(output)
            tempList = stListCopy.copy()
            for studentInstance in stListCopy:
                if isinstance(studentInstance, Student):
                    for name in pollStList:
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

                        last: str = studentInstance.getStudentLastName()
                        tempLastName = last.split(" ")

                        # TODO birden fazla soyadi olanlari unutmayin
