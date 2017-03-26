app.controller("regionCtrl", ['RegionService', 'PersonService', 'ModalProvider', 'FileService', '$scope', '$rootScope', '$state', '$timeout',
    function (RegionService, PersonService, ModalProvider, FileService, $scope, $rootScope, $state, $timeout) {

        $scope.selected = {};

        $scope.fetchTableData = function () {
            $rootScope.showNotify("المناطق", "فضلاً انتظر قليلاً حتى الانتهاء من تحميل المناطق", "warning", "fa-map-marker");
            RegionService.fetchTableData().then(function (data) {
                $scope.regions = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("المناطق", "تم الانتهاء من تحميل البيانات المطلوبة بنجاح، يمكنك متابعة عملك الآن", "success", "fa-map-marker");
            });
        };

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.regions, function (region) {
                    if (object.id == region.id) {
                        $scope.selected = region;
                        return region.isSelected = true;
                    } else {
                        return region.isSelected = false;
                    }
                });
            }
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openRegionCreateModel();
        };

        $scope.openUpdateModel = function (region) {
            if (region) {
                ModalProvider.openRegionUpdateModel(region);
                return;
            }
            ModalProvider.openRegionUpdateModel($scope.selected);
        };

        $scope.delete = function (region) {
            if (region) {
                RegionService.remove(region);
                return;
            }
            RegionService.remove($scope.selected);
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
                    $scope.openUpdateModel($itemScope.region);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-minus-square-o fa-lg"></span> حذف</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.region);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);