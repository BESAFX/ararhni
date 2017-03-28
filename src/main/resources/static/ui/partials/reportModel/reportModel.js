app.controller("reportModelCtrl", ['ReportModelService', 'PersonService', 'ModalProvider', 'FileService', '$scope', '$rootScope', '$state', '$timeout',
    function (ReportModelService, PersonService, ModalProvider, FileService, $scope, $rootScope, $state, $timeout) {

        $scope.selected = {};

        $scope.fetchTableData = function () {
            $rootScope.showNotify("نماذج الطباعة", "فضلاً انتظر قليلاً حتى الانتهاء من تحميل النماذج", "warning", "fa-print");
            ReportModelService.findAll().then(function (data) {
                $scope.reportModels = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("نماذج الطباعة", "تم الانتهاء من تحميل البيانات المطلوبة بنجاح، يمكنك متابعة عملك الآن", "success", "fa-print");
            });
        };

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.reportModels, function (reportModel) {
                    if (object.id == reportModel.id) {
                        $scope.selected = reportModel;
                        return reportModel.isSelected = true;
                    } else {
                        return reportModel.isSelected = false;
                    }
                });
            }
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openReportModelCreateModel();
        };

        $scope.openUpdateModel = function (reportModel) {
            if (reportModel) {
                ModalProvider.openReportModelUpdateModel(reportModel);
                return;
            }
            ModalProvider.openReportModelUpdateModel($scope.selected);
        };

        $scope.delete = function (reportModel) {
            if (reportModel) {
                ReportModelService.remove(reportModel);
                return;
            }
            ReportModelService.remove($scope.selected);
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
                    $scope.openUpdateModel($itemScope.reportModel);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-minus-square-o fa-lg"></span> حذف</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.reportModel);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);