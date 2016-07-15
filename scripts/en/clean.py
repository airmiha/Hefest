"""
Script for checking and cleaning duplicates. Note that there is no ; case covered!
"""
import pdb
with open('ServiceCategories.csv', 'r') as fin:
    for line in fin:
        items = [x.strip() for x in line.split(',') if len(x.strip()) > 2]
        if len(items) < 1:
            continue       
        tail = set(items[1:])
        #print (items[0], tail)
        res = ','.join([items[0]] + [x for x in tail])
        print(res)
