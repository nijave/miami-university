parent(male1, male2).
parent(female1, male2).
parent(male1, male3).
parent(female1, male3).
parent(male1, female2).
parent(female1, female2).

parent(male2, male4).
parent(female3, male4).
parent(male2, male8).
parent(female3, male8).

parent(male3, female5).
parent(female4, female5).
parent(male3, male5).
parent(female4, male5).

parent(male4, female7).
parent(female6, feamle7).
parent(male4, male7).
parent(female6, male7).

parent(male8, female9).
parent(female8, female9).

married(male1, female1).
married(male2, female3).
married(male3, female4).
married(female2, male6).
married(male4, female6).
married(male8, female8).
married(male7, female10).

married0(S, X) :- married(S, X).
married0(S, X) :- married(X, S).

siblings(X, Y) :- parent(Z, X), parent(Z, Y), \+ X = Y.
greatuncleaunt(X, Z) :- siblings(X, Y), parent(Y, A), parent(A, Z).
greatuncleaunt(S, Z) :- married0(S, X), siblings(X, Y), parent(Y, A), parent(A, Z).
