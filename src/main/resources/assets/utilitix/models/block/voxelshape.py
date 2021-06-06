#!/usr/bin/env python3

import json
import sys
from typing import List

print('Detailed:')
print('VoxelShapes.or(')
with open(sys.argv[1]) as file:
    data = json.load(file)
    elems: List[dict] = data['elements']
    for i in range(0, len(elems)):
        elem = elems[i]
        endStr = ''
        if i < len(elems) - 1:
            endStr =','
        if not 'rotation' in elem or elem['rotation']['angle'] == 0:
            print(f'    makeCuboidShape({elem["from"][0]}, {elem["from"][1]}, {elem["from"][2]}, {elem["to"][0]}, {elem["to"][1]}, {elem["to"][2]})' + endStr)
print(');')

print()
print('Simple:')
minX = None
minY = None
minZ = None
maxX = None
maxY = None
maxZ = None
with open(sys.argv[1]) as file:
    data = json.load(file)
    elems: List[dict] = data['elements']
    for i in range(0, len(elems)):
        elem = elems[i]
        endStr = ''
        if i < len(elems) - 1:
            endStr =','
        if not 'rotation' in elem or elem['rotation']['angle'] == 0:
            if minX is None or elem["from"][0] < minX:
                minX = elem["from"][0]
            if minX is None or elem["to"][0] < minX:
                minX = elem["to"][0]

            if minY is None or elem["from"][1] < minY:
                minY = elem["from"][1]
            if minY is None or elem["to"][1] < minY:
                minY = elem["to"][1]

            if minZ is None or elem["from"][2] < minZ:
                minZ = elem["from"][2]
            if minZ is None or elem["to"][2] < minZ:
                minZ = elem["to"][2]

            if maxX is None or elem["from"][0] > maxX:
                maxX = elem["from"][0]
            if maxX is None or elem["to"][0] > maxX:
                maxX = elem["to"][0]

            if maxY is None or elem["from"][1] > maxY:
                maxY = elem["from"][1]
            if maxY is None or elem["to"][1] > maxY:
                maxY = elem["to"][1]

            if maxZ is None or elem["from"][2] > maxZ:
                maxZ = elem["from"][2]
            if maxZ is None or elem["to"][2] > maxZ:
                maxZ = elem["to"][2]
print(f'makeCuboidShape({minX}, {minY}, {minZ}, {maxX}, {maxY}, {maxZ});')

