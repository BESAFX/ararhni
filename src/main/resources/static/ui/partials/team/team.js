app.controller("teamCtrl", ['TeamService', 'ModalProvider', '$rootScope', '$scope', '$timeout', '$state',
    function (TeamService, ModalProvider, $rootScope, $scope, $timeout, $state) {

        $scope.selected = {};

        $scope.fetchTableData = function () {
            $rootScope.showNotify("المجموعات", "فضلاً انتظر قليلاً حتى الانتهاء من تحميل المجموعات", "warning", "fa-shield");
            TeamService.findAll().then(function (data) {
                $scope.teams = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("المجموعات", "تم الانتهاء من تحميل البيانات المطلوبة بنجاح، يمكنك متابعة عملك الآن", "success", "fa-shield");
            });
        };

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.teams, function (team) {
                    if (object.id == team.id) {
                        $scope.selected = team;
                        return team.isSelected = true;
                    } else {
                        return team.isSelected = false;
                    }
                });
            }
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openTeamCreateModel();
        };

        $scope.openUpdateModel = function (team) {
            if (team) {
                ModalProvider.openTeamUpdateModel(team);
                return;
            }
            ModalProvider.openTeamUpdateModel($scope.selected);
        };

        $scope.delete = function (team) {
            if (team.persons.length == 0) {
                TeamService.remove(team.id).then(function () {
                    $rootScope.showNotify("المجموعات", "تم الحذف بنجاح، يمكنك متابعة عملك الآن", "success", "fa-shield");
                });
            } else {
                $rootScope.showNotify("المجموعات", "لا يمكنك الحذف نظراً لاستخدامها من قبل بعض المستخدمين", "error", "fa-shield");
            }
        };

        $scope.rowMenu = [
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-plus-square-o fa-lg"></span> اضافة</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openCreateModel();
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-edit fa-lg"></span> تعديل</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openUpdateModel($itemScope.team);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-minus-square-o fa-lg"></span> حذف</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.team);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);
