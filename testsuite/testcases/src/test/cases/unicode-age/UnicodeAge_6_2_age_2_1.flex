%%

%unicode 6.2
%public
%class UnicodeAge_6_2_age_2_1

%type int
%standalone

%include ../../resources/common-unicode-enumerated-property-defined-values-only-java

%%

<<EOF>> { printOutput(); return 1; }
\p{Age:2.1} { setCurCharPropertyValue("Age:2.1"); }
[^] { }
