; zipcodes.scm contains all the US zipcodes.
; You should not modify this file. Your code
; should work for other instances of this file.
(load "zipcodes.scm")

; Helper function
(define (mydisplay value)
	(display value)
	(newline)
	#t
)

; Returns the roots of the quadratic formula, given
; ax^2+bx+c=0
(define (quadratic a b c)
	(cond 
		((and (= a 0) (= b 0))
			'() ; No solution when a,b = 0
		)
		((= a 0) 
			(list (/ (* -1 c) b)) ; linear, 1 solution
		)
		(else
			(list
				(/ (+ (* b -1) (sqrt (- (* b b) (* 4 (* a c))))) (* 2 a))
				(/ (- (* b -1) (sqrt (- (* b b) (* 4 (* a c))))) (* 2 a)) 
			)
		)
	)
	;'(0 0)
)

;(mydisplay (quadratic 1 0 0))
;(mydisplay (quadratic 0 1 0))
;(mydisplay (quadratic 3 4 2))

; Return a list with the items in reverse order
(define (reverse lst)
	(if (null? lst) '()
		 (append 
		  	(reverse (cdr lst)) (list (car lst))
		 )
	)
)

;(mydisplay (reverse '(1 2 3 4)))

; Returns a list that is identical to lst, but with all
; instances of v1 replaced with v2.
; (replace '(a b c (c a b) b a) 'a 'b) -> (b b c (c b b) b b)
; lst -- list of items, possibly nested.
; v1 & v2 -- atoms
; 565 students only
;(define (replace lst v1 v2)
;	lst
;)

;(mydisplay (replace '(a b c c b a) 'a 'b))
;(mydisplay (replace '(a b c (c a b) b a) 'a 'b))

; copied from list.scm
(define (maxelt lst)
  (if (= (lengthgth lst) 1) (car lst) (max (car lst) (maxelt (cdr lst))))
)
(define (minelt lst)
  (if (= (lengthgth lst) 1) (car lst) (min (car lst) (minelt (cdr lst))))
)

; Returns a list of two numeric values. The first is the smallest
; in the list and the second is the largest in the list. 
; lst -- flat, contains numeric values, and length is >= 1.
(define (minAndMax lst)
	(list (minelt lst) (maxelt lst))
)

;(mydisplay (minAndMax '(4 3)))

; Returns a list of three numbers (numNeg numZero numPos),
; where these numbers correspond to the number of negative
; numbers, number of zeros, and the number of positive numbers.
; For example (posneg '(-9 2 3 0 -2 -8 0)) should return
; (3 2 2). Approximately, 25% of this problem's points will be
; awarded for doing this with just one pass through the list.
; lst -- flat list containing numeric values, and length is >= 1.

; helper--makes list and another list of counts (of neg,zero,pos)
(define (posnegHelper lst vals)
  (cond ((null? lst) vals) ; empty list--return values (000)
		((< (car lst) 0) (posnegHelper (cdr lst) (list (+ 1 (car vals)) (cadr vals) (caddr vals)))) ;neg, increment first
		((= (car lst) 0) (posnegHelper (cdr lst) (list (car vals) (+ 1 (cadr vals)) (caddr vals)))) ;zero, increment second
		((> (car lst) 0) (posnegHelper (cdr lst) (list (car vals) (cadr vals) (+ 1 (caddr vals))))) ;pos, increment third
		(else lst)
  )
)
(define (posneg lst)
	(posnegHelper lst '(0 0 0))
)

;(mydisplay (posneg '(1 2 3 4 2 0 -2 3 23 -3)))
;(mydisplay (posneg '(-1 2 -3 4 2 0 -2 3 -23 -3 0 0)))
;(mydisplay (posneg '()))

; The paramters are two flat lists with the same length.
; The inputs '(1 2 3) and '(a b c) should return a single list:
; ((1 a) (2 b) (3 c))
; lst1 & lst2 -- two flat lists with same length.
(define (zipper lst1 lst2 out)
  (cond ((null? lst1) out)
		((null? lst2) out) ;not really needed but just in case they're different
		(else 
		 (zipper (cdr lst1)
				 (cdr lst2)
				 (append out (list(list (car lst1) (car lst2))))
		 )
		)
  )
)

(define (zip lst1 lst2)
	(zipper lst1 lst2 '())
)

;(mydisplay (zip '("Smith" "Jackson" "Wilson") '(35 28 21)))

; Returns all the information for a particular zip code
; zipcode -- 5 digit integer
; zips -- the zipcode DB
(define (getZipcodeInfo zipcode zips)
  (cond 
    ((null? zips) "Zipcode not found")
   	((equal? (caar zips) zipcode) (car zips))
	(else (getZipcodeInfo zipcode (cdr zips)))
  )
)

;(mydisplay (getZipcodeInfo '22 zipcodes))

; Returns a list of all the states that contain the given place.
; The list of states should list the relevant states only once.
; placeName -- is the text corresponding to the name of the place
; zips -- the zipcode DB
(define (thisPlaceHelper placeName zips states)
  (cond 
     ((null? zips) states)
     ((equal? placeName (cadar zips)) (append states (list (caddar zips)) (thisPlaceHelper placeName (cdr zips) states)))
     (else (thisPlaceHelper placeName (cdr zips) states))
  )
)
(define (getStatesThatContainThisPlace placeName zips)
	(thisPlaceHelper placeName zips '())
)

;(mydisplay (getStatesThatContainThisPlace "Oxford" zipcodes))

; Returns the state that contains the most unique zip codes
; Return the first state if there is a tie
; zips -- zipcode DB
(define (mostZipsHelper zips most cur)
	(cond
		((null? zips) (cadr most)) ; zips are gone, return most
		((equal? (caddar zips) (cadr cur))
			(mostZipsHelper (cdr zips) most (list (+ (car cur) 1) (cadr cur)))
		)
		(else
			(if (> (car cur) (car most))
				(mostZipsHelper (cdr zips) cur (list 1 (caddar zips)))
				(mostZipsHelper (cdr zips) most (list 1 (caddar zips)))
			)
		)
	)
)

(define (getStateWithMostZipcodes zips)
	(mostZipsHelper zips '(0 "") '(0 ""))
)

;(mydisplay (getStateWithMostZipcodes zipcodes))

; Returns the distance between two zip codes.
; Use lat/lon. Do some research to compute this.
; zip2 -- zipcode DB
; zip1 & zip2 -- the two zip codes in question.
; 565 students only
;(define (getDistanceBetweenZipCodes zips zip1 zip2)
;	0
;)
;(mydisplay (getZipcodeInfo 45056 zipcodes))
;(mydisplay (getStatesThatContainThisPlace "Oxford" zipcodes))
;(mydisplay (getStateWithMostZipcodes zipcodes))
;(mydisplay (getDistanceBetweenZipCodes zipcodes 45056 48122))


; Some sample predicates
(define (POS? x) (> x 0))
(define (NEG? x) (> x 0))
(define (LARGE? x) (>= (abs x) 10))
(define (SMALL? x) (NOT (LARGE? x)))

; Returns a list of items that satisfy a set of predicates.
; For example (filterList '(1 2 3 4 100) '(EVEN?)) should return the even numbers (2 4 100)
; (filterList '(1 2 3 4 100) '(EVEN? SMALL?)) should return (2 4)
; lst -- flat list of items
; filters -- list of predicates to apply to the individual elements
(define (filter lst func)
	(cond
		((null? lst) '())
		((null? func) lst)
		(else
			(if (eval (list func (car lst)) user-initial-environment)
				(cons (car lst) (filter (cdr lst) func))
				(filter (cdr lst) func)
			)
		)
	)
)

(define (filterList lst filters)
	(if (null? filters) 
		lst
		(filterList (filter lst (car filters)) (cdr filters))
	)
)

;(mydisplay (filterList '(1 2 3 11 22 33 -1 -2 -3 -11 -22 -33) '(POS?)))
;(mydisplay (filterList '(1 2 3 11 22 33 -1 -2 -3 -11 -22 -33) '(POS? EVEN?)))
;(mydisplay (filterList '(1 2 3 11 22 33 -1 -2 -3 -11 -22 -33) '(POS? EVEN? LARGE?)))