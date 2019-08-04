# Compila e gera o executável debugável
g++ ./mytest.cpp -g -o mytest.exe

# Faz o debug
gdb ./mytest.exe

# Remove arquivos desnecessários
rm *.exe
rm ./TesteArvore.txt
rm *.stackdump
rm *.o
