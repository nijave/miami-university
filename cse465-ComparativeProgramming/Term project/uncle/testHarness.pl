query(greatuncleaunt(male6, female7)).
query(greatuncleaunt(male6, male8)).
query(greatuncleaunt(female7, male3)).
query(greatuncleaunt(female4, female7)).
query(greatuncleaunt(female4, male3)).
query(greatuncleaunt(male3, male7)).
query(greatuncleaunt(male6, female7)).
query(greatuncleaunt(female7, male6)).
query(greatuncleaunt(male5, female8)).
query(greatuncleaunt(male5, male8)).
query(greatuncleaunt(male8, female8)).

writeln(T) :- write(T), nl.

main :- consult(uncle),
	forall(query(Q), (Q->writeln(yes:Q) ; writeln(no:Q))),
	halt.
:- initialization(main).
