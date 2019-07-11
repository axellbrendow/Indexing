echo "DOXYFILE_ENCODING = 'UTF-8'
PROJECT_NAME = 'ArvoreB'
INPUT = '../ArvoreB'
OUTPUT_DIRECTORY = 'doc'
GENERATE_HTML = 'YES'" > doxyconfig.txt

doxygen doxyconfig.txt