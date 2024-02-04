/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * --------------------------------------------
 * @file AdminLTE layout.ts
 * @description Layout for AdminLTE.
 * @license MIT
 * --------------------------------------------
 */

import {onDOMContentLoaded} from './util/index'

/**
 * ------------------------------------------------------------------------
 * Constants
 * ------------------------------------------------------------------------
 */

const CLASS_NAME_HOLD_TRANSITIONS = 'hold-transition'
const CLASS_NAME_APP_LOADED = 'app-loaded'

/**
 * Class Definition
 * ====================================================
 */

class Layout {
  _element: HTMLElement

  constructor(element: HTMLElement) {
    this._element = element
  }

  holdTransition(): void {
    let resizeTimer: ReturnType<typeof setTimeout>
    window.addEventListener('resize', () => {
      document.body.classList.add(CLASS_NAME_HOLD_TRANSITIONS)
      clearTimeout(resizeTimer)
      resizeTimer = setTimeout(() => {
        document.body.classList.remove(CLASS_NAME_HOLD_TRANSITIONS)
      }, 400)
    })
  }
}

onDOMContentLoaded(() => {
  const data = new Layout(document.body)
  data.holdTransition()
  setTimeout(() => {
    document.body.classList.add(CLASS_NAME_APP_LOADED)
  }, 400)
})

export default Layout
