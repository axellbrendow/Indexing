#include "templates/debug.hpp"

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
////
    std::vector<int> vec1{ 1, 2, 3 };
    std::vector<int> vec2{ 4, 5 };

    vec2.insert(
        vec2.begin() + 1,
        make_move_iterator( vec1.begin() ),
        make_move_iterator( vec1.end() )
    );

    debugIterar(vec1.begin(), vec1.end());
    debugIterar(vec2.begin(), vec2.end());
////

    return 0;
}