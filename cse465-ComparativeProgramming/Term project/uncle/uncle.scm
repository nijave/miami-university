(load "relations.scm")

;zmuda (some file)
(define (mydisplay value)
	(display value)
	(newline)
	#t
)
;zmuda list.scm
(define (ismember? atm lst)
	(cond
		((null? lst) #f)
		((equal? atm (car lst)) #t)
		(else (ismember? atm (cdr lst)))
	)
)

(define (join a b)
  (cond 
	((null? b) a)
	(else (join (cons (car b) a) (cdr b)))
  )
)

(define (getParentsHelper person parent parents)
  (cond 
     ((null? parent) parents)
     ((equal? person (cadar parent)) (append parents (list (caar parent)) (getParentsHelper person (cdr parent) parents)))
     (else (getParentsHelper person (cdr parent) parents))
  )
)
(define (getParents person)
	(getParentsHelper person parent '())
)
(define (getParentsListHelper people parent parents)
	(cond
		((null? people) parents)
		(else(append parents (getParents (car people)) (getParentsListHelper (cdr people) parent parents)))
	)
)
(define (getParentsList people)
	(getParentsListHelper people parent '())
)

(define (getChildrenHelper person parent children)
	(cond 
		((null? parent) children)
		((equal? person (caar parent)) (append children (list (cadar parent)) (getChildrenHelper person (cdr parent) children)))
		(else (getChildrenHelper person (cdr parent) children))
	)
)

(define (getChildren person)
	(getChildrenHelper person parent '())
)

(define (getSiblingsHelperBlood person parents siblings)
	(if (null? parents)
	(siblings)
	(append siblings 
		(getChildren (car parents))
		(getChildren (cdr parents))
	)
	)
)

(define (getSiblingsHelperMarriage person marriage siblings)
	(cond 
		((null? marriage) siblings)
		((equal? person (caar marriage)) (append siblings (list (cadar marriage)) (getSiblingsHelperMarriage person (cdr marriage) siblings)))
		((equal? person (cadar marriage)) (append siblings (list (caar marriage)) (getSiblingsHelperMarriage person (cdr marriage) siblings)))
		(else (getSiblingsHelperMarriage person (cdr marriage) siblings))
	)
)

(define (getSiblings person)
	(join 
		(if (null? (getParents person)) '() (getSiblingsHelperBlood person (getParents person) '()))
		(getSiblingsHelperMarriage person marriage '())
	)
)
(define (getSiblingsListHelper people siblings)
	(cond
		((null? people) siblings)
		(else(append siblings (getSiblings (car people)) (getSiblingsListHelper (cdr people) siblings)))
	)
)
(define (getSiblingsList people)
	(getSiblingsListHelper people '())
)
; need getSiblingList twice to get married people correctly--the first pass won't get everyone (just blood and married to the parameter)
(define (greatuncle person1 person2)
	(ismember? person1 (getSiblingsList (getSiblingsList (getParentsList (getParents person2)))))
)

(greatuncle "male6" "female7")
(greatuncle "male6" "male8")
(greatuncle "female7" "male3")
(greatuncle "female4" "female7")
(greatuncle "female4" "male3")
(greatuncle "male3" "male7")
(greatuncle "male6" "female7")
(greatuncle "female7" "male6")
(greatuncle "male5" "female8")
(greatuncle "male5" "male8")
(greatuncle "male8" "female8")
(mydisplay "test cases done")
,exit


