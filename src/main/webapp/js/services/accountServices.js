hefest.factory('Activate', ['$resource',
    function ($resource) {
        return $resource('resources/account/activate', {}, {
            'get': { method: 'GET', params: {}, isArray: false}
        });
    }]);

hefest.factory('ForgotPassword', ['$resource',
     function ($resource) {		 
         return $resource('resources/account/forgotpassword', {}, {
      });
 }]);

hefest.factory('ResetPassword', ['$resource',
    function ($resource) {
        return $resource('resources/account/resetpassword', {}, {
     });
}]);

hefest.factory('Password', ['$resource',
    function ($resource) {
        return $resource('resources/account/changepassword', {}, {
        });
}]);

hefest.factory('Account', ['$resource',
    function ($resource) {
        return $resource('resources/account/current', {}, {
        });
}]);

hefest.constant('USER_ROLES', {
        all: '*',
        admin: 'ROLE_ADMIN',
        professional: 'ROLE_PROFESSIONAL',
        user: 'ROLE_USER'
});

hefest.factory('Session', [
    function () {
        this.create = function (name, email, role) {
            this.name = name;
            this.email = email;
            this.role = role;
        };
        this.invalidate = function () {
        	 this.name = null;
             this.email = null;
             this.role = null;
        };
        return this;
    }]);


hefest.factory('AuthenticationSharedService', ['$rootScope', '$http', 'authService', 'Session', 'Account',
    function ($rootScope, $http, authService, Session, Account) {
	
		 var isAuthorized = function (authorizedRoles) {
	         if (!angular.isArray(authorizedRoles)) {
	             if (authorizedRoles === '*') {
	                 return true;
	             }
	
	             authorizedRoles = [authorizedRoles];
	         }
	
	         var isAuthorized = false;
	         angular.forEach(authorizedRoles, function(authorizedRole) {
	             var authorized = (!!Session.email &&
	                 Session.role === authorizedRole);
	
	             if (authorized || authorizedRole === '*') {
	            	 isAuthorized = true;
	             }
	         });
	
	         return isAuthorized;
	     };
	
        return {
            login: function (param) {
                var data ="j_username=" + param.username +"&j_password=" + param.password +"&_spring_security_remember_me=" + param.rememberMe +"&submit=Login";
  
                $http.post('account/authentication', data, {
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    ignoreAuthModule: 'ignoreAuthModule'
                }).success(function (data, status, headers, config) {                	
                 	Account.get(function(data) {
                        Session.create(data.name, data.email, data.role);
                        $rootScope.account = Session;
                        authService.loginConfirmed(data);
                    });
                }).error(function (data, status, headers, config) {
                    $rootScope.authenticationError = true;
                    Session.invalidate();
                });
            },
            valid: function (authorizedRoles) {
                $http.get('protected/transparent.gif?' + new Date().getTime(), {
                    ignoreAuthModule: 'ignoreAuthModule'
                }).success(function (data, status, headers, config) {
                    if (!Session.email) {
                        Account.get(function(data) {
                            Session.create(data.name, data.email, data.role);
                            $rootScope.account = Session;
                            if (!isAuthorized(authorizedRoles)) {                               
                                // user is not allowed
                                $rootScope.$broadcast("event:auth-notAuthorized");
                            }
                            $rootScope.authenticated = true;
                        });
                    }
                    $rootScope.authenticated = !!Session.email;
                }).error(function (data, status, headers, config) {                	
                    $rootScope.authenticated = false;
                });
            },
            
            logout: function () {
                $rootScope.authenticationError = false;
                $rootScope.authenticated = false;
                $rootScope.account = null;

                $http.post('resources/account/logout');
                Session.invalidate();
                authService.loginCancelled();
            }
        };
    }]);