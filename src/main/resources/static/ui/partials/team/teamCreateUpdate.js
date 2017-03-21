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
                $rootScope.showToast("جاري تحميل بيانات خاصة بالشاشة، فضلاَ انتظر قليلاً");
                RoleService.findByTeam(team.id).then(function (data) {
                    $scope.roles = data;
                    $rootScope.showToast("تم تحميل البيانات بنجاح");
                });
            } else {
                $scope.team = {};
                $rootScope.showToast("جاري تحميل بيانات خاصة بالشاشة، فضلاَ انتظر قليلاً");
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
                    $rootScope.showToast("تم تحميل البيانات بنجاح");
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
                $rootScope.showToast("جاري القيام بالعملية، فضلاً انتظر قليلاً");
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
                            $rootScope.showToast("تم القيام بالعملية بنجاح، يمكنك اضافة مجموعة اخرى الآن");
                        });
                        break;
                    case 'update' :
                        RoleService.setUpRoles($scope.roles).then(function (data) {
                            $scope.team = data[0].team;
                            $scope.roles = data;
                            $rootScope.showToast("تم القيام بالعملية بنجاح، يمكنك متابعة عملك الآن");
                        });
                        break;
                }
            };

            $scope.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };

        }]);