,open random
(define MAXRANDINT (expt 2.0 28))
(define randomInt (make-random 34))
(define (randomDouble) (/ (randomInt) MAXRANDINT))

(define (randlist len) (if (= len 0) '() (cons (randomDouble) (randlist (- len 1)))))

(define (listSum lst) (if (null? lst) 0 (+ (car lst) (listSum (cdr lst)))))

(define (run) (listSum (randlist 1000000)))
(run)
,exit
