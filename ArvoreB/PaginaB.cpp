#include <iostream>

using namespace std;

class myclass
{
    public:
        int x;

        myclass(int x) : x(x) {}

        myclass &operator>> (int quantity)
        {
            for (size_t i = 0; i < quantity; i++)
            {
                x = x * 2;
            }

            return *this;
        }
};

ostream& operator<< (ostream &stream, myclass &myclass)
{
    return stream << "num = " << myclass.x;
}