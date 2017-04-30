app.controller("employeeCtrl", ['EmployeeService', 'ModalProvider', 'FileService', '$scope', '$rootScope', '$log', '$http', '$state', '$timeout',
    function (EmployeeService, ModalProvider, FileService, $scope, $rootScope, $log, $http, $state, $timeout) {

        $scope.selected = {};

        $scope.fetchTableData = function () {
            $rootScope.showNotify("الموظفين", "فضلاً انتظر قليلاً حتى الانتهاء من تحميل الموظفين", "warning", "fa-user-circle");
            EmployeeService.fetchTableData().then(function (data) {
                $scope.employees = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("الموظفين", "تم الانتهاء من تحميل البيانات المطلوبة بنجاح، يمكنك متابعة عملك الآن", "success", "fa-user-circle");
            })
        };

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.employees, function (employee) {
                    if (object.id == employee.id) {
                        $scope.selected = employee;
                        return employee.isSelected = true;
                    } else {
                        return employee.isSelected = false;
                    }
                });
            }
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openEmployeeCreateModel();
        };

        $scope.openUpdateModel = function (employee) {
            if (employee) {
                ModalProvider.openEmployeeUpdateModel(employee);
                return;
            }
            ModalProvider.openEmployeeUpdateModel($scope.selected);
        };

        $scope.delete = function (employee) {
            if (employee) {
                EmployeeService.remove(employee);
                return;
            }
            EmployeeService.remove($scope.selected);
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
                    $scope.openUpdateModel($itemScope.employee);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-minus-square-o fa-lg"></span> حذف</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.employee);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);