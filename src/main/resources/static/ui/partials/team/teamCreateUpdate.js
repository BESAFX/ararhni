app.controller('teamCreateUpdateCtrl',
    ['ScreenService', 'TeamService', 'RoleService', 'NotificationProvider', 'notifyCode', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'team',
        function (ScreenService, TeamService, RoleService, NotificationProvider, notifyCode, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, team) {

            $scope.$watch('buffer.createAll', function (newValue, oldValue) {
                angular.forEach($scope.roles, function (role) {
                    role.permission.createEntity = newValue;
                });
            }, true);

            $scope.$watch('buffer.updateAll', function (newValue, oldValue) {
                angular.forEach($scope.roles, function (role) {
                    role.permission.updateEntity = newValue;
                });
            }, true);

            $scope.$watch('buffer.deleteAll', function (newValue, oldValue) {
                angular.forEach($scope.roles, function (role) {
                    role.permission.deleteEntity = newValue;
                });
            }, true);

            $scope.$watch('buffer.reportAll', function (newValue, oldValue) {
                angular.forEach($scope.roles, function (role) {
                    role.permission.reportEntity = newValue;
                });
            }, true);

            $scope.roles = [];
            if (team) {
                $scope.team = team;
                $rootScope.showNotify("المجموعات", "جاري تحميل بيانات خاصة بالشاشة، فضلاَ انتظر قليلاً", "warning", "fa-shield");
                RoleService.findByTeam(team.id).then(function (data) {
                    $scope.roles = data;
                    $rootScope.showNotify("المجموعات", "تم تحميل البيانات بنجاح", "success", "fa-shield");
                });
            } else {
                $scope.team = {};
                $rootScope.showNotify("المجموعات", "جاري تحميل بيانات خاصة بالشاشة، فضلاَ انتظر قليلاً", "warning", "fa-shield");
                ScreenService.findAll().then(function (data) {
                    for (var i = 0; i < data.length; i++) {
                        var role = {};
                        role.permission = {};
                        role.permission.createEntity = false;
                        role.permission.updateEntity = false;
                        role.permission.deleteEntity = false;
                        role.permission.reportEntity = false;
                        role.permission.screen = data[i];
                        $scope.roles.push(role);
                    }
                    $rootScope.showNotify("المجموعات", "تم تحميل البيانات بنجاح", "success", "fa-shield");
                });
            }

            $scope.changeAllRow = function (role) {
                role.permission.createEntity = role.all;
                role.permission.updateEntity = role.all;
                role.permission.deleteEntity = role.all;
                role.permission.reportEntity = role.all;
            };

            $scope.title = title;

            $scope.action = action;

            $timeout(function () {
                $('#name').focus();
            }, 500);

            $scope.submit = function () {
                $rootScope.showNotify("المجموعات", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-shield");
                for (var i = 0; i < $scope.roles.length; i++) {
                    $scope.roles[i].team = $scope.team;
                }
                switch ($scope.action) {
                    case 'create' :
                        RoleService.setUpRoles($scope.roles).then(function (data) {
                            $scope.team = {};
                            $scope.roles = [];
                            ScreenService.findAll().then(function (data) {
                                for (var i = 0; i < data.length; i++) {
                                    var role = {};
                                    role.permission = {};
                                    role.permission.createEntity = false;
                                    role.permission.updateEntity = false;
                                    role.permission.deleteEntity = false;
                                    role.permission.reportEntity = false;
                                    role.permission.fullControlEntity = false;
                                    role.permission.screen = data[i];
                                    $scope.roles.push(role);
                                }
                            });
                            $scope.from.$setPristine();
                            $rootScope.showNotify("المجموعات", "تم القيام بالعملية بنجاح، يمكنك اضافة مجموعة اخرى الآن", "success", "fa-shield");
                        });
                        break;
                    case 'update' :
                        RoleService.setUpRoles($scope.roles).then(function (data) {
                            $scope.team = data[0].team;
                            $scope.roles = data;
                            $rootScope.showNotify("المجموعات", "تم القيام بالعملية بنجاح، يمكنك متابعة عملك الآن", "success", "fa-shield");
                        });
                        break;
                }
            };

            $scope.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };

        }]);