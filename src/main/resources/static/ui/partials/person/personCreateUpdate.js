app.controller('personCreateUpdateCtrl', ['TeamService', 'PersonService', 'FileUploader', 'NotificationProvider', 'FileService', 'notifyCode', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'person',
    function (TeamService, PersonService, FileUploader, NotificationProvider, FileService, notifyCode, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, person) {

        $scope.fetchTeamData = function () {
            TeamService.findAll().then(function (data) {
                $scope.teams = data;
                $rootScope.showToast("تم تحميل بيانات المجموعات بنجاح");
            });
        };

        $timeout(function () {
            $rootScope.showToast("جاري تحميل بيانات المجموعات، فضلاَ انتظر قليلاً");
            $scope.fetchTeamData();
        }, 2000);

        if (person) {
            $scope.person = person;
            if (person.photo) {
                FileService.getSharedLink(person.photo).then(function (data) {
                    $scope.logoLink = data;
                });
            }
        } else {
            $scope.person = {};
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            $rootScope.showToast("جاري القيام بالعملية، فضلاً انتظر قليلاً");
            switch ($scope.action) {
                case 'create' :
                    PersonService.create($scope.person).then(function (data) {
                        $scope.person = {};
                        $scope.from.$setPristine();
                        $rootScope.showToast("تم القيام بالعملية بنجاح، يمكنك اضافة حساب مستخدم آخر الآن");
                    });
                    break;
                case 'update' :
                    PersonService.update($scope.person).then(function (data) {
                        $scope.person = data;
                        $rootScope.showToast("تم القيام بالعملية بنجاح، يمكنك متابعة عملك الآن");
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        var uploader = $scope.uploader = new FileUploader({
            url: 'uploadFile'
        });

        uploader.filters.push({
            name: 'syncFilter',
            fn: function (item, options) {
                console.log('syncFilter');
                return this.queue.length < 10;
            }
        });

        uploader.filters.push({
            name: 'asyncFilter',
            fn: function (item, options, deferred) {
                console.log('asyncFilter');
                setTimeout(deferred.resolve, 1e3);
            }
        });

        uploader.onAfterAddingFile = function (fileItem) {
            uploader.uploadAll();
        };
        uploader.onSuccessItem = function (fileItem, response, status, headers) {
            $scope.person.photo = response;
            FileService.getSharedLink(response).then(function (data) {
                $scope.logoLink = data;
            });
        };

    }]);