/* Services */

hefest.factory('Municipalities', ['$resource', function ($resource) {
    return $resource('resources/municipalities', {}, {});
}]);

hefest.factory('Counties', ['$resource', function ($resource) {
    return $resource('resources/counties', {}, {});
}]);

hefest.factory('Localities', ['$resource', function ($resource) {
    return $resource('resources/localities', {}, {});
}]);

hefest.factory('Professional', ['$resource', function ($resource) {
    return $resource('resources/professionals/:professionalId', {professionalId: '@id'},  {
        query: {method:'GET', params:{}, cache : true}
    });
}]);

hefest.factory('User', ['$resource', function ($resource) {
    return $resource('resources/users/:userId', {userId: '@id'},  {});
}]);

hefest.factory('ProfessionalAccount', ['$resource', function ($resource) {
    return $resource('resources/professionals/current', {},  {});
}]);

hefest.factory('ServiceCategory', ['$resource', function ($resource) {
    return $resource('resources/servicecategories/:serviceCategoryId', {serviceCategoryId: '@id'}, { 
    });
}]);

hefest.factory('ServiceCategories', ['$resource', function ($resource) {
     return $resource('resources/professions/:professionId/servicecategories', {professionId: '@id'}, { 
     });
}]);
     
hefest.factory('Professions', ['$resource', function ($resource) {
     return $resource('resources/professions', {}, {});
}]);

hefest.factory('ProfessionsAsTree', ['$resource', function ($resource) {
    return $resource('resources/professionsAsTree', {}, {});
}]);

hefest.factory('Items', ['$resource', function ($resource) {
    return $resource('resources/items', {}, {});
}]);

hefest.factory('ServiceRequests', ['$resource', function ($resource) {
    return $resource('resources/users/current/servicerequests', {}, {});
}]);

hefest.factory('Projects', ['$resource', function ($resource) {
    return $resource('resources/projects/:projectId', {projectId: '@id'},  {
        query: {method:'GET', params:{}, cache : true}
    });
}]);

hefest.factory('Endorsements', ['$resource', function ($resource) {
    return $resource('resources/professionals/:professionalId/endorsements', {professionalId: '@id'}, {
        query: {method:'GET', params:{}, cache : true}
    });
}]);

hefest.factory('Testimonials', ['$resource', function ($resource) {
    return $resource('resources/professionals/:professionalId/testimonials', {professionalId: '@id'}, {
        query: {method:'GET', params:{}, cache : true}
    });
}]);
