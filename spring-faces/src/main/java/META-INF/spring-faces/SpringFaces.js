SpringFaces = {};
		
SpringFaces.advisors = [];

SpringFaces.advisorsApplied = false;
		
SpringFaces.applyAdvisors = function(){
	if (!SpringFaces.advisorsApplied) {		
		for (var x=0; x<SpringFaces.advisors.length; x++) {
			SpringFaces.advisors[x].apply();
		}
		SpringFaces.advisorsApplied = true;
	}
};

SpringFaces.validateAll = function(){
	var valid = true;
	for(x in SpringFaces.advisors) {
		if (SpringFaces.advisors[x].decorator &&
			!SpringFaces.advisors[x].validate()) {
			valid = false;
		}
	}
	return valid;
};