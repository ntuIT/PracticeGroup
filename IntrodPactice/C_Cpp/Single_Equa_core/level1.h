#ifdef MATHFUNCSDLL_EXPORTS
#define MATHFUNCSDLL_API __declspec(dllexport) 
#else
#define MATHFUNCSDLL_API __declspec(dllimport) 
#endif
#include <string.h>
#include <string>
#include <math.h>
#include <iostream>
using namespace std;
namespace Single_Equation_level1
{
     class Equation_1
     {
     protected: double fact_1, fact_0;
     public:
	   Equation_1();
	   Equation_1(double a, double b);
	   //Equation_1(string a, string b);

	   char *string2archar(string s);
	   void Set_a(double a);
	   void Set_b(double b);
	   double Get_a();
	   double Get_b();
	   string Solve();
	   string toString();
	   double function(double x);

	   ~Equation_1();

     };

}

namespace  Single_Equation_level2
{
     class Equation_2 : Single_Equation_level1::Equation_1
     {
	   double fact_2;
     public:
	   Equation_2();
	   Equation_2(double a, double b, double c);

	   void Set_a(double a);
	   void Set_b(double b);
	   void Set_c(double c);
	   double Get_a();
	   double Get_b();
	   double Get_c();
	   double function(double x);
	   double Delta();
	   string Solve(Equation_1 &tapnghiem);
	   string toString();

	   ~Equation_2();
     };
}