using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Nearby.RNNearby
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNNearbyModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNNearbyModule"/>.
        /// </summary>
        internal RNNearbyModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNNearby";
            }
        }
    }
}
