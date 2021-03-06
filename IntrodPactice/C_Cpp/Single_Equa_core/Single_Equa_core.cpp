// Single_Equa_core.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"

#include "level1.h"
namespace Single_Equation_level1
{
     char *Equation_1::string2archar(string s)
     {
	   char* c = new char[1 + s.size() ];
	   int i = 0, n = s.size();
	   //copy(s.begin(), s.end(), c);
	   for (i; i < n; i++)
		 c[i] = s[i];
	   c[s.size()] = '\0';
	   return c;
     }
     Equation_1::Equation_1()
     {}
     Equation_1::Equation_1(double a, double b)
     {
	   this->fact_1 = a;
	   this->fact_0 = b;
     }
     /*
     Equation_1::Equation_1(string a, string b)
     {
	   this->fact_1 = atoi(string2archar(a));
	   this->fact_0 = atoi(string2archar(b));
     } */
     Equation_1::~Equation_1()
     {}

     void Equation_1::Set_a(double a)
     {
	   this->fact_1 = a;
     }
     void Equation_1::Set_b(double b)
     {
	   this->fact_0 = b;
     }
     double Equation_1::Get_a()
     {
	   return this->fact_1;
     }
     double Equation_1::Get_b()
     {
	   return this->fact_0;
     }

     string Equation_1::Solve()
     {
	   string result("");
	   if (this->fact_1 != 0)
	   {
		 if (0 != fact_0)
		 {
		      double nghiem = -(this->fact_0) / this->fact_1;
		      result += nghiem;
		 }
	   }
	   else
	   {
		 if (0 == this->fact_0)
		      result = "infinity";
	   }
	   return result;
     }
     string Equation_1::toString()
     {
	   string result(""); bool flag = false;
	   if (0 != fact_1)
	   {
		 if (fact_1 > 0)
		 {
		      result += fact_1;
		      result += " x  ";
		      if (fact_0 > 0)
			    result += "+ ";
		      if(0!=fact_0) 
			    result += fact_0;
		 }
		 else
		 {
		      if (fact_0  <0)
		      {
			    result += fact_1;
			    result += " x  ";
			    if (0 != fact_0)
				  result += fact_0;
		      }
		      else
		      {
			    if (0 != fact_0)
				  result += fact_0;
			    result += " ";
			    if (fact_0 > 0)
				  result += "+ ";
			    result += fact_1;
			    result += " x  ";
		      }
		 }
		  flag = true;
	   }
	   if ((0 != fact_0) && (flag==false)) result += fact_0;
	   return result;
     }
     double Equation_1::function(double x)
     {
	   return this->fact_1 * x + this->fact_0;
     }
}

namespace  Single_Equation_level2
{
     Equation_2::Equation_2()
     {}
     Equation_2::Equation_2(double a, double b, double c)
     {
	   this->Set_a(a);
	   this->Set_b(b);
	   this->Set_c(c);
     }
     Equation_2::~Equation_2()
     {}
     void Equation_2::Set_a(double a)
     {
	   this->fact_2 = a;
     }
     void Equation_2::Set_b(double b)
     {
	   this->fact_1 = b;
     }
     void Equation_2::Set_c(double c)
     {
	   this->fact_0 = c;
     }
     double Equation_2::Get_a()
     {
	   return this->fact_2;
     }
     double Equation_2::Get_b()
     {
	   return this->fact_1;
     }
     double Equation_2::Get_c()
     {
	   return this->fact_0;
     }
     double Equation_2::function(double x)
     {
	   return (this->fact_2 * pow(x,2) + this->fact_1 *x + this->fact_0 );
     }
     double Equation_2::Delta()
     {
	   return ( pow(this->fact_1, 2) - 4 * this->fact_2*this->fact_0 );
     }
     string Equation_2::Solve(Equation_1 &tapnghiem)
     {
	   string result("");
	   double dt = this->Delta();
	   if (dt > 0)
	   {
		 if (dt == 0)
		      result = "double";
		 else result = "different";
		 tapnghiem.Set_a( ( sqrt(dt) - this->fact_1)/(2*this->fact_2) ); // (-b + can(đt))/2a
		 tapnghiem.Set_b( - (sqrt(dt) - this->fact_1) / (2 * this->fact_2) ); // (-b-can(dt)) /2a
	   }
	   return result;
     }
     string Equation_2::toString()
     {
	   string re("");
	   if (0 != this->fact_2)
	   {
		 re += this->fact_2;
		 re += " x² ";
	   }
	   if (0 != this->fact_1)
	   {
		 if (this->fact_1 > 0)
		      re += " +";
		 re += this->fact_1;
		 re += " x ";
	   }
	   if (0 != this->fact_0)
	   {
		 if (this->fact_0 > 0)
		      re += " +";
		 re += this->fact_0;
	   }
	   return re;
     }
     //
}
