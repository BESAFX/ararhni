app.controller("departmentCtrl", ['DepartmentService', 'ModalProvider', '$scope', '$rootScope', '$log', '$http', '$state', '$timeout',
    function (DepartmentService, ModalProvider, $scope, $rootScope, $log, $http, $state, $timeout) {

        $scope.selected = {};

        $scope.fetchTableData = function () {
            $rootScope.showNotify("الاقسام", "فضلاً انتظر قليلاً حتى الانتهاء من تحميل الأقسام", "warning", "fa-sitemap");
            DepartmentService.fetchTableData().then(function (data) {
                console.info(data);
                $scope.departments = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("الاقسام", "تم الانتهاء من تحميل البيانات المطلوبة بنجاح، يمكنك متابعة عملك الآن", "success", "fa-sitemap");
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

        $scope.delete = function (department) {
            if (department) {
                DepartmentService.remove(department);
                return;
            }
            DepartmentService.remove($scope.selected);
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