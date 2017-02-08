% The real roots of Ax^2+Bx+C=0 are returned in the listen
% ROOTS
quadratic(0, 0, C, []).
quadratic(0, B, C, [D]) :- D is C/B.
quadratic(A, 0, C, [D]) :- D is C/sqrt(A), !. 
quadratic(A, B, C, [R1, R2]) :- 
		D is sqrt(B*B - 4*A*C), 
		R1 is (-B + D)/(2*A), 
		R2 is (-B - D)/(2*A).

% The minimum and maximum values of the integer list, LST,
% are returned in the second parameter.
lstmin([M], M).
lstmin([H|T], Min) :- lstmin(T, M), Min is min(H, M).
lstmax([M], M).
lstmax([H|T], Max) :- lstmax(T, M), Max is max(H, M).
minmax(LST, [A, B]) :- lstmin(LST, A), lstmax(LST, B).

% Two flat lists of equal length are zipped into a new
% list - the third parameter ZIP.
zip([], [], []).
zip([H1|T1], [H2|T2], ZIP) :- append([[H1,H2]], Z, ZIP), zip(T1, T2, Z).

% Succeeds if the list of integers can be cleved into two
% sections that both sum to the same value.
list_sum([], 0).
list_sum([H|T], SUM) :- list_sum(T, S), SUM is H + S.
splitable(LST, L1, L2) :- append(L1, L2, LST), list_sum(L1, A), list_sum(L2, B), A=B.

% S1, S2, and S3 are flat lists representing a set of integers. 
% S3 is the union of S1 and S2.
union([], [], []).
union([], L2, U) :- \+ L2=[], union(L2, [], U).
union([H|T], L2, U) :- union(T, L2, U), memberchk(H,U).
union([H|T], L2, U) :- union(T, L2, A), \+ memberchk(H, A), append([H], A, U).


% Succeeds if LST is a list of integers in ascending order.
issorted([]).
issorted([_]).
issorted([H1|[H2|T]]) :- H1 =< H2, append([H2], T, LST), issorted(LST).

% Given any combination of input parameters, finds
% consistent variable instatiations.
getStateInfo(Place, State, Zip) :- location(Zip, Place, State, _, _, _).

% succeeds if P1 and P2 are Mth cousins N times removed.
% Insert of fictional family tree for testing.
mthCousinNTimesRemoved(P1, P2, M, N) :- P1=abe, P2=jeremy, M=8, N=5.
