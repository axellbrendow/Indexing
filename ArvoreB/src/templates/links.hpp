/**
 * Frases de Bjarne Stroustrup (o pai do C++):<br>
 * 
 * https://kdfrases.com/autor/bjarne-stroustrup
 * 
 * “C faz com que dar um tiro no pé seja fácil; C++ torna isso mais difícil,
 * mas quando nós o fazemos rebenta com a perna toda.” ― Bjarne Stroustrup
 * 
 * Links úteis doxygen:<br>
 * 
 * https://flcwiki.desy.de/How%20to%20document%20your%20code%20using%20doxygen#How_to_create_a_configuration_file<br>
 * 
 * Links úteis smart pointers:<br>
 * 
 * https://docs.microsoft.com/pt-br/cpp/cpp/smart-pointers-modern-cpp?view=vs-2019<br>
 * https://docs.microsoft.com/pt-br/cpp/cpp/how-to-create-and-use-shared-ptr-instances?view=vs-2019<br>
 * 
 * Variadic Templates (∞+ QI):<br>
 * 
 * https://eli.thegreenplace.net/2014/variadic-templates-in-c/
 * 
 * Introduction to Type Traits:<br>
 * 
 * http://blog.aaronballman.com/2011/11/a-simple-introduction-to-type-traits/
 * 
 * Makefiles:<br>
 * 
 * https://www.youtube.com/watch?v=_r7i5X0rXJk
 * 
 * lvalue, rvalue, xvalue, glvalue, prvalue:<br>
 * 
 * https://pt.stackoverflow.com/questions/6373/o-que-s%C3%A3o-os-rvalues-lvalues-xvalues-glvalues-e-prvalues
 * 
 * Assembly registers:<br>
 * 
 * https://www.tutorialspoint.com/assembly_programming/assembly_registers
 * https://wiki.skullsecurity.org/Registers
 * 
 * eax -> extended accumulator registers pair (general-purpose)
 * ebx -> extended base registers pair (general-purpose)
 * ecx -> extended count registers pair (general-purpose)
 * edx -> extended data registers pair (general-purpose)
 * esi -> extended source index
 * edi -> extended destination index
 * ebp -> extended begging pointer (esp when function is called)
 * esp -> extended stack pointer (32-bit)
 * rsp -> register stack pointer (64-bit)
 * 
 * GDB Segmentation Fault:<br>
 * 
 * https://stackoverflow.com/questions/9809810/gdb-corrupted-stack-frame-how-to-debug
 * 
 * 32-bit
 * (gdb) set $pc = *(void **)$esp
 * (gdb) set $esp = $esp + 4
 * 
 * 64-bit
 * (gdb) set $pc = *(void **)$rsp
 * (gdb) set $rsp = $rsp + 8
 */