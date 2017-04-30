app.controller("companyCtrl", ['CompanyService', 'ModalProvider', 'FileService', '$scope', '$rootScope', '$log', '$http', '$state', '$timeout',
    function (CompanyService, ModalProvider, FileService, $scope, $rootScope, $log, $http, $state, $timeout) {

        $scope.selected = {};

        $scope.fetchTableData = function () {
            $rootScope.showNotify("الشركات", "فضلاً انتظر قليلاً حتى الانتهاء من تحميل البيانات", "warning", "fa-bank");
            CompanyService.fetchTableData().then(function (data) {
                $scope.companies = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("الشركات", "تم الانتهاء من تحميل البيانات المطلوبة بنجاح، يمكنك متابعة عملك الآن", "success", "fa-bank");
            })
        };

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.companies, function (company) {
                    if (object.id == company.id) {
                        $scope.selected = company;
                        return company.isSelected = true;
                    } else {
                        return company.isSelected = false;
                    }
                });
            }
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openCompanyCreateModel();
        };

        $scope.openUpdateModel = function (company) {
            if (company) {
                ModalProvider.openCompanyUpdateModel(company);
                return;
            }
            ModalProvider.openCompanyUpdateModel($scope.selected);
        };

        $scope.delete = function (company) {
            if (company) {
                CompanyService.remove(company);
                return;
            }
            CompanyService.remove($scope.selected);
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
                    $scope.openUpdateModel($itemScope.company);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-minus-square-o fa-lg"></span> حذف</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.company);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);