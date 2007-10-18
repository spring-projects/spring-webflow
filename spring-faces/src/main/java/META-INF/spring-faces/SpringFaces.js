SpringFaces = {};
		
SpringFaces.advisors = [];
		
SpringFaces.applyAdvisors = function(){
			
	for (var x=0; x<SpringFaces.advisors.length; x++) {
		SpringFaces.advisors[x].apply();
	}
};

SpringFaces.validateAll = function(){
	var valid = true;
	for(x in SpringFaces.advisors) {
		if (SpringFaces.advisors[x].decorator &&
			!SpringFaces.advisors[x].decorator.validate()) {
			valid = false;
		}
	}
	return valid;
};