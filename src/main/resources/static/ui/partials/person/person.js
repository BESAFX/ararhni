app.controller("personCtrl", ['PersonService', 'ModalProvider', 'FileService', '$scope', '$rootScope', '$state', '$timeout',
    function (PersonService, ModalProvider, FileService, $scope, $rootScope, $state, $timeout) {

        $scope.selected = {};

        $scope.fetchTableData = function () {
            $rootScope.showNotify("حسابات المستخدمين", "فضلاً انتظر قليلاً حتى الانتهاء من تحميل حسابات المستخدمين", "warning", "fa-user");
            PersonService.findAll().then(function (data) {
                $scope.persons = data;
                $scope.setSelected(data[0]);
                $rootScope.showNotify("حسابات المستخدمين", "تم الانتهاء من تحميل البيانات المطلوبة بنجاح، يمكنك متابعة عملك الآن", "success", "fa-user");
                angular.forEach(data, function (person) {
                    if(person.photo){
                        FileService.getSharedLink(person.photo).then(function (data) {
                            return person.pic = data;
                        });
                    }
                })
            });
        };

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.persons, function (person) {
                    if (object.id == person.id) {
                        $scope.selected = person;
                        return person.isSelected = true;
                    } else {
                        return person.isSelected = false;
                    }
                });
            }
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openPersonCreateModel();
        };

        $scope.openUpdateModel = function (person) {
            if (person) {
                ModalProvider.openPersonUpdateModel(person);
                return;
            }
            ModalProvider.openPersonUpdateModel($scope.selected);
        };

        $scope.openReportPersonsModel = function () {
            ModalProvider.openPersonsReportModel($scope.persons);
        };

        $scope.delete = function (person) {
            if (person) {
                PersonService.remove(person);
                return;
            }
            PersonService.remove($scope.selected);
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
                    $scope.openUpdateModel($itemScope.person);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-minus-square-o fa-lg"></span> حذف</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.person);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-print fa-lg"></span> طباعة تقرير مختصر </div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openReportPersonsModel();
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);