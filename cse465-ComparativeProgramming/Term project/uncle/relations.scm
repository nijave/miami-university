(define parent
	'(
	("root" "male1")
	("root" "female1")
	("male1" "male2")
	("female1" "male2")
	("male1" "male3")
	("female1" "male3")
	("male1" "female2")
	("female1" "female2")
	
	("male2" "male4")
	("female3" "male4")
	("male2" "male8")
	("female3" "male8")
	
	("male3" "female5")
	("female4" "female5")
	("male3" "male5")
	("female4" "male5")
	
	("male4" "female7")
	("female6" "feamle7")
	("male4" "male7")
	("female6" "male7")
	
	("male8" "female9")
	("female8" "female9")
	)
)

(define marriage
	'(
	("male1" "female1")
	("male2" "female3")
	("male3" "female4")
	("female2" "male6")
	("male4" "female6")
	("male8" "female8")
	("male7" "female10")
	)
)