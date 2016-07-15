hefest.service('Utility', function() {
	'use strict';
	this.getObjectsFromMetadata = function(metadata, data) {
		var i = 0;
		var columnIndexes = {};
		for(var column in metadata) {
			if(metadata.hasOwnProperty(column)) {	
				columnIndexes[i] = metadata[column];
				i++;
			}
		}

		var objects = [];	
		var j = 0;
		for(var key in data) {
			if(data.hasOwnProperty(key)) {								
				var object = {};
				var row = data[key];						
				var k = 0;				
				for (var value in row) {	
					if(row.hasOwnProperty(value)) {	
						var cell = columnIndexes[k];								
						object[cell] = row[value];
						k++;
					}
				}
				
				//TODO: Just a temporary fix
				if (object.avgreview) {
					object.avgreview = Number(object.avgreview).toFixed(2);
					object.avgReviewAsInt = Math.round(object.avgreview);
				} 	
				
				object.yearsinbusiness = Math.floor(Math.random() * 30) + 1;
				
				object.index = j++;
				objects.push(object);
			}
		}
		return objects;
	};
});

hefest.factory('MetricsService', ['$resource', function ($resource) {
	return $resource('metrics/metrics', {}, {
		'get': { method: 'GET'}
     });
}]);

hefest.factory('ThreadDumpService', ['$http',  function ($http) {
	return {
      dump: function() {
          var promise = $http.get('dump').then(function(response){
                  return response.data;
              });
              return promise;
          }
      };
}]);

hefest.factory('HealthCheckService', ['$rootScope', '$http', function ($rootScope, $http) {
	return {
      check: function() {
          var promise = $http.get('health').then(function(response){
              return response.data;
          });
          return promise;
      }
	};
}]);