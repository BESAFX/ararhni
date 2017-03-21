app.controller('reportModelCreateUpdateCtrl', ['ReportModelService', 'ScreenService', 'PersonService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'reportModel',
    function (ReportModelService, ScreenService, PersonService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, reportModel) {

        $scope.fetchScreenData = function () {
            ScreenService.findAll().then(function (data) {
                $scope.screens = data;
                $rootScope.showToast("تعليمات", "تم تحميل بيانات الشاشات بنجاح");
            });
        };

        if (reportModel) {
            $scope.reportModel = reportModel;
            $scope.reportProp = JSON.parse($scope.reportModel.template);
        } else {

            $scope.reportModel = {};

            $scope.reportProp = {};

            $scope.reportProp.exportType = 'pdf';

            $scope.reportProp.orientation = 'Portrait';

            $scope.reportProp.title = 'تقرير مهام إدارية';

            $scope.reportProp.columns = [
                {
                    "name": "رقم المهمة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "عنوان المهمة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "تفاصيل المهمة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "تاريخ إنشاء المهمة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center", "viewProp": false
                },
                {
                    "name": "تاريخ تسليم المهمة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center", "viewProp": false
                },
                {
                    "name": "جهة التكليف",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "رقم الحركة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "تاريخ الحركة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "محتوى الحركة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                },
                {
                    "name": "مدخل الحركة",
                    "view": false,
                    "groupBy": false,
                    "sortBy": false,
                    "dataTextAlign": "Center",
                    "viewProp": false
                }
            ];
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            $rootScope.showToast("تعليمات", "جاري القيام بالعملية، فضلاً انتظر قليلاً");
            $scope.reportModel.template = JSON.stringify($scope.reportProp);
            switch ($scope.action) {
                case 'create' :
                    ReportModelService.create($scope.reportModel).then(function (data) {
                        $scope.reportModel = {};
                        $scope.form.$setPristine();
                        $rootScope.showToast("تعليمات", "تم القيام بالعملية بنجاح، يمكنك اضافة نموذج آخر الآن");
                    });
                    break;
                case 'update' :
                    ReportModelService.update($scope.reportModel).then(function (data) {
                        $scope.reportModel = data;
                        $rootScope.showToast("تعليمات", "تم القيام بالعملية بنجاح، يمكنك متابعة عملك الآن");
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);