import json
import os
import sys

sys.path.insert(1, os.path.abspath('../bl-passenger-service'))
sys.path.insert(2, os.path.abspath('bl-passenger-service'))

from generate_people import generate_storyline_passengers

if __name__ == '__main__':
    passengers = generate_storyline_passengers(300)
    with open('./../../bl-simulation-data/passengers/passengers.json', 'w') as file:
        file.write(json.dumps(passengers))