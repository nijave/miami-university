makeLst(Lst, N) :- length(Lst, N), maplist(random, L).
sumList([], 0).
sumList([H1|T], Sum+H1) :- sumList(T, Sum).
