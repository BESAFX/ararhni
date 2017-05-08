app.controller("departmentCtrl", ['DepartmentService', 'ModalProvider', '$scope', '$rootScope', '$log', '$http', '$state', '$timeout',
    function (DepartmentService, ModalProvider, $scope, $rootScope, $log, $http, $state, $timeout) {

        $scope.selected = {};

        $scope.fetchTableData = function () {
            DepartmentService.fetchTableData().then(function (data) {
                $scope.departments = data;
                $scope.setSelected(data[0]);
            });
        };

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.departments, function (department) {
                    if (object.id == department.id) {
                        $scope.selected = department;
                        return department.isSelected = true;
                    } else {
                        return department.isSelected = false;
                    }
                });
            }
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openDepartmentCreateModel();
        };

        $scope.openUpdateModel = function (department) {
            if (department) {
                ModalProvider.openDepartmentUpdateModel(department);
                return;
            }
            ModalProvider.openDepartmentUpdateModel($scope.selected);
        };

        $scope.removeRow = function (id) {
            var index = -1;
            var departmentsArr = eval($scope.departments);
            for (var i = 0; i < departmentsArr.length; i++) {
                if (departmentsArr[i].id === id) {
                    index = i;
                    break;
                }
            }
            if (index === -1) {
                alert("Something gone wrong");
            }
            $scope.departments.splice(index, 1);
        };

        $scope.delete = function (department) {
            if (department) {
                $rootScope.showConfirmNotify("المهام", "هل تود حذف القسم فعلاً؟", "error", "fa-database", function () {
                    DepartmentService.remove(department.id).then(function () {
                        $scope.removeRow(department.id);
                    });
                });
                return;
            }
            $rootScope.showConfirmNotify("المهام", "هل تود حذف القسم فعلاً؟", "error", "fa-database", function () {
                DepartmentService.remove($scope.selected.id).then(function () {
                    $scope.removeRow(department.id);
                });
            });
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
                    $scope.openUpdateModel($itemScope.department);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-minus-square-o fa-lg"></span> حذف</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.department);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);