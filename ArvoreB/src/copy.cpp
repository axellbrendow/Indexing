#include <iostream>
#include <vector>

class Base
{
public:
    int one()
    {
        return 1;
    }
};

class Derived : public Base
{
public:
    using Base::one;

    int one(int x)
    {
        return x + one();
    }
};

int main()
{
    // Derived obj;

    // std::cout << obj.one(7) << std::endl;
    std::vector<int> vec{ 1, 2, 3 };

    vec.resize(6);

    for (size_t i = 0; i < vec.capacity(); i++)
    {
        std::cout << vec.at(i) << ",";
    }
    std::cout << std::endl;

    return 0;
}