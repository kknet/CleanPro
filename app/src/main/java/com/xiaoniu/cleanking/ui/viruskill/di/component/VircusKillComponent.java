/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaoniu.cleanking.ui.viruskill.di.component;

import android.app.Activity;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.xiaoniu.cleanking.ui.viruskill.contract.VirusKillContract;
import com.xiaoniu.cleanking.ui.viruskill.di.module.UserModule;

import dagger.BindsInstance;
import dagger.Component;

/**
 */
@ActivityScope
@Component(modules = UserModule.class, dependencies = AppComponent.class)
public interface VircusKillComponent {
    void inject(Activity activity);
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder view(VirusKillContract.View view);
        Builder appComponent(AppComponent appComponent);
        VircusKillComponent build();
    }
}