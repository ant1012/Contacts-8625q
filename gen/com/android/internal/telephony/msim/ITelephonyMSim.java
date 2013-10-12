/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/ant/workspace/android/Contacts_qrd/src/com/android/internal/telephony/msim/ITelephonyMSim.aidl
 */
package com.android.internal.telephony.msim;
/**
 * Interface used to interact with the phone.  Mostly this is used by the
 * TelephonyManager class.  A few places are still using this directly.
 * Please clean them up if possible and use TelephonyManager instead.
 *
 * {@hide}
 */
public interface ITelephonyMSim extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.android.internal.telephony.msim.ITelephonyMSim
{
private static final java.lang.String DESCRIPTOR = "com.android.internal.telephony.msim.ITelephonyMSim";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.android.internal.telephony.msim.ITelephonyMSim interface,
 * generating a proxy if needed.
 */
public static com.android.internal.telephony.msim.ITelephonyMSim asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.android.internal.telephony.msim.ITelephonyMSim))) {
return ((com.android.internal.telephony.msim.ITelephonyMSim)iin);
}
return new com.android.internal.telephony.msim.ITelephonyMSim.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_dial:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
this.dial(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_call:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
this.call(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_showCallScreen:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.showCallScreen();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_showCallScreenWithDialpad:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.showCallScreenWithDialpad(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_endCall:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.endCall(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_answerRingingCall:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.answerRingingCall(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_silenceRinger:
{
data.enforceInterface(DESCRIPTOR);
this.silenceRinger();
reply.writeNoException();
return true;
}
case TRANSACTION_isOffhook:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.isOffhook(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isRinging:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.isRinging(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isIdle:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.isIdle(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isRadioOn:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.isRadioOn(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isSimPinEnabled:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.isSimPinEnabled(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_cancelMissedCallsNotification:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.cancelMissedCallsNotification(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_supplyPin:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
boolean _result = this.supplyPin(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_supplyPuk:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
boolean _result = this.supplyPuk(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_supplyPinReportResult:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _result = this.supplyPinReportResult(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_supplyPukReportResult:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
int _result = this.supplyPukReportResult(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_handlePinMmi:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
boolean _result = this.handlePinMmi(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_toggleRadioOnOff:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.toggleRadioOnOff(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setRadio:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _arg1;
_arg1 = data.readInt();
boolean _result = this.setRadio(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_updateServiceLocation:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.updateServiceLocation(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_enableApnType:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.enableApnType(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_disableApnType:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.disableApnType(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_enableDataConnectivity:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.enableDataConnectivity();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_disableDataConnectivity:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.disableDataConnectivity();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isDataConnectivityPossible:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isDataConnectivityPossible();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getCallState:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getCallState(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDataActivity:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDataActivity();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDataState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDataState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getActivePhoneType:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getActivePhoneType(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCdmaEriIconIndex:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getCdmaEriIconIndex(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCdmaEriIconMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getCdmaEriIconMode(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCdmaEriText:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getCdmaEriText(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_needsOtaServiceProvisioning:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.needsOtaServiceProvisioning();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getVoiceMessageCount:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getVoiceMessageCount(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getNetworkType:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getNetworkType(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_hasIccCard:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.hasIccCard(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getLteOnCdmaMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getLteOnCdmaMode(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getIccPin1RetryCount:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getIccPin1RetryCount(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getAllCellInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<android.telephony.CellInfo> _result = this.getAllCellInfo();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getDefaultSubscription:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDefaultSubscription();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getPreferredVoiceSubscription:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPreferredVoiceSubscription();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getPreferredDataSubscription:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPreferredDataSubscription();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setPreferredDataSubscription:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.setPreferredDataSubscription(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isSimPukLocked:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.isSimPukLocked(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isSubActive:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.isSubActive(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.android.internal.telephony.msim.ITelephonyMSim
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * Dial a number. This doesn't place the call. It displays
     * the Dialer screen for that subscription.
     * @param number the number to be dialed. If null, this
     * would display the Dialer screen with no number pre-filled.
     * @param subscription user preferred subscription.
     */
@Override public void dial(java.lang.String number, int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(number);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_dial, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Place a call to the specified number on particular subscription.
     * @param number the number to be called.
     * @param subscription user preferred subscription.
     */
@Override public void call(java.lang.String number, int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(number);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_call, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * If there is currently a call in progress, show the call screen.
     * The DTMF dialpad may or may not be visible initially, depending on
     * whether it was up when the user last exited the InCallScreen.
     *
     * @return true if the call screen was shown.
     */
@Override public boolean showCallScreen() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_showCallScreen, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Variation of showCallScreen() that also specifies whether the
     * DTMF dialpad should be initially visible when the InCallScreen
     * comes up.
     *
     * @param showDialpad if true, make the dialpad visible initially,
     *                    otherwise hide the dialpad initially.
     * @return true if the call screen was shown.
     *
     * @see showCallScreen
     */
@Override public boolean showCallScreenWithDialpad(boolean showDialpad) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((showDialpad)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_showCallScreenWithDialpad, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * End call on particular subscription or go to the Home screen
     * @param subscription user preferred subscription.
     * @return whether it hung up
     */
@Override public boolean endCall(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_endCall, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Answer the currently-ringing call on particular subscription.
     *
     * If there's already a current active call, that call will be
     * automatically put on hold.  If both lines are currently in use, the
     * current active call will be ended.
     *
     * TODO: provide a flag to let the caller specify what policy to use
     * if both lines are in use.  (The current behavior is hardwired to
     * "answer incoming, end ongoing", which is how the CALL button
     * is specced to behave.)
     *
     * TODO: this should be a oneway call (especially since it's called
     * directly from the key queue thread).
     *
     * @param subscription user preferred subscription.
     */
@Override public void answerRingingCall(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_answerRingingCall, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Silence the ringer if an incoming call is currently ringing.
     * (If vibrating, stop the vibrator also.)
     *
     * It's safe to call this if the ringer has already been silenced, or
     * even if there's no incoming call.  (If so, this method will do nothing.)
     *
     * TODO: this should be a oneway call too (see above).
     *       (Actually *all* the methods here that return void can
     *       probably be oneway.)
     */
@Override public void silenceRinger() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_silenceRinger, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Check if a particular subscription has an active or holding call
     *
     * @param subscription user preferred subscription.
     * @return true if the phone state is OFFHOOK.
     */
@Override public boolean isOffhook(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_isOffhook, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Check if an incoming phone call is ringing or call waiting
     * on a particular subscription.
     *
     * @param subscription user preferred subscription.
     * @return true if the phone state is RINGING.
     */
@Override public boolean isRinging(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_isRinging, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Check if the phone is idle on a particular subscription.
     *
     * @param subscription user preferred subscription.
     * @return true if the phone state is IDLE.
     */
@Override public boolean isIdle(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_isIdle, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Check to see if the radio is on or not on particular subscription.
     * @param subscription user preferred subscription.
     * @return returns true if the radio is on.
     */
@Override public boolean isRadioOn(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_isRadioOn, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Check if the SIM pin lock is enable
     * for particular subscription.
     * @param subscription user preferred subscription.
     * @return true if the SIM pin lock is enabled.
     */
@Override public boolean isSimPinEnabled(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_isSimPinEnabled, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Cancels the missed calls notification on particular subscription.
     * @param subscription user preferred subscription.
     */
@Override public void cancelMissedCallsNotification(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_cancelMissedCallsNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Supply a pin to unlock the SIM for particular subscription.
     * Blocks until a result is determined.
     * @param pin The pin to check.
     * @param subscription user preferred subscription.
     * @return whether the operation was a success.
     */
@Override public boolean supplyPin(java.lang.String pin, int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pin);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_supplyPin, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Supply puk to unlock the SIM and set SIM pin to new pin.
     *  Blocks until a result is determined.
     * @param puk The puk to check.
     *        pin The new pin to be set in SIM
     * @param subscription user preferred subscription.
     * @return whether the operation was a success.
     */
@Override public boolean supplyPuk(java.lang.String puk, java.lang.String pin, int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(puk);
_data.writeString(pin);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_supplyPuk, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Supply a pin to unlock the SIM.  Blocks until a result is determined.
     * Returns a specific success/error code.
     * @param pin The pin to check.
     * @param subscription user preferred subscription.
     * @return Phone.PIN_RESULT_SUCCESS on success. Otherwise error code
     */
@Override public int supplyPinReportResult(java.lang.String pin, int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pin);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_supplyPinReportResult, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Supply puk to unlock the SIM and set SIM pin to new pin.
     * Blocks until a result is determined.
     * Returns a specific success/error code.
     * @param puk The puk to check
     *        pin The pin to check.
     * @param subscription user preferred subscription.
     * @return Phone.PIN_RESULT_SUCCESS on success. Otherwise error code
     */
@Override public int supplyPukReportResult(java.lang.String puk, java.lang.String pin, int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(puk);
_data.writeString(pin);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_supplyPukReportResult, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Handles PIN MMI commands (PIN/PIN2/PUK/PUK2), which are initiated
     * without SEND (so <code>dial</code> is not appropriate) for
     * a particular subscription.
     * @param dialString the MMI command to be executed.
     * @param subscription user preferred subscription.
     * @return true if MMI command is executed.
     */
@Override public boolean handlePinMmi(java.lang.String dialString, int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(dialString);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_handlePinMmi, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Toggles the radio on or off on particular subscription.
     * @param subscription user preferred subscription.
     */
@Override public void toggleRadioOnOff(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_toggleRadioOnOff, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Set the radio to on or off on particular subscription.
     * @param subscription user preferred subscription.
     */
@Override public boolean setRadio(boolean turnOn, int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((turnOn)?(1):(0)));
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_setRadio, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Request to update location information for a subscrition in service state
     * @param subscription user preferred subscription.
     */
@Override public void updateServiceLocation(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_updateServiceLocation, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Enable a specific APN type.
     */
@Override public int enableApnType(java.lang.String type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(type);
mRemote.transact(Stub.TRANSACTION_enableApnType, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Disable a specific APN type.
     */
@Override public int disableApnType(java.lang.String type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(type);
mRemote.transact(Stub.TRANSACTION_disableApnType, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Allow mobile data connections.
     */
@Override public boolean enableDataConnectivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_enableDataConnectivity, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Disallow mobile data connections.
     */
@Override public boolean disableDataConnectivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disableDataConnectivity, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Report whether data connectivity is possible.
     */
@Override public boolean isDataConnectivityPossible() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isDataConnectivityPossible, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Returns the call state for a subscription.
     */
@Override public int getCallState(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_getCallState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getDataActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDataActivity, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getDataState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDataState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Returns the current active phone type as integer for particular subscription.
     * Returns TelephonyManager.PHONE_TYPE_CDMA if RILConstants.CDMA_PHONE
     * and TelephonyManager.PHONE_TYPE_GSM if RILConstants.GSM_PHONE
     * @param subscription user preferred subscription.
     */
@Override public int getActivePhoneType(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_getActivePhoneType, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Returns the CDMA ERI icon index to display on particular subscription.
     * @param subscription user preferred subscription.
     */
@Override public int getCdmaEriIconIndex(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_getCdmaEriIconIndex, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Returns the CDMA ERI icon mode on particular subscription,
     * 0 - ON
     * 1 - FLASHING
     * @param subscription user preferred subscription.
     */
@Override public int getCdmaEriIconMode(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_getCdmaEriIconMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Returns the CDMA ERI text for particular subscription,
     * @param subscription user preferred subscription.
     */
@Override public java.lang.String getCdmaEriText(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_getCdmaEriText, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Returns true if OTA service provisioning needs to run.
     * Only relevant on some technologies, others will always
     * return false.
     */
@Override public boolean needsOtaServiceProvisioning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_needsOtaServiceProvisioning, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Returns the unread count of voicemails for a subscription.
     * @param subscription user preferred subscription.
     * Returns the unread count of voicemails
     */
@Override public int getVoiceMessageCount(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_getVoiceMessageCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Returns the network type of a subscription.
     * @param subscription user preferred subscription.
     * Returns the network type
     */
@Override public int getNetworkType(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_getNetworkType, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Return true if an ICC card is present for a subscription.
     * @param subscription user preferred subscription.
     * Return true if an ICC card is present
     */
@Override public boolean hasIccCard(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_hasIccCard, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Return if the current radio is LTE on CDMA. This
     * is a tri-state return value as for a period of time
     * the mode may be unknown.
     *
     * @return {@link Phone#LTE_ON_CDMA_UNKNOWN}, {@link Phone#LTE_ON_CDMA_FALSE}
     * or {@link PHone#LTE_ON_CDMA_TRUE}
     */
@Override public int getLteOnCdmaMode(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_getLteOnCdmaMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Gets the number of attempts remaining for PIN1/PUK1 unlock
     * for a subscription.
     * @param subscription user preferred subscription.
     * Gets the number of attempts remaining for PIN1/PUK1 unlock.
     */
@Override public int getIccPin1RetryCount(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_getIccPin1RetryCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Returns the all observed cell information of the device.
     */
@Override public java.util.List<android.telephony.CellInfo> getAllCellInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<android.telephony.CellInfo> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAllCellInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(android.telephony.CellInfo.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * get default subscription
     * @return subscription id
     */
@Override public int getDefaultSubscription() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDefaultSubscription, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * get user prefered voice subscription
     * @return subscription id
     */
@Override public int getPreferredVoiceSubscription() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPreferredVoiceSubscription, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * get user prefered data subscription
     * @return subscription id
     */
@Override public int getPreferredDataSubscription() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPreferredDataSubscription, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
     * Set user prefered data subscription
     * @return true if success
     */
@Override public boolean setPreferredDataSubscription(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_setPreferredDataSubscription, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isSimPukLocked(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_isSimPukLocked, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
     * Get subscription is activated or not
     * @return true if subscription is activated
     */
@Override public boolean isSubActive(int subscription) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(subscription);
mRemote.transact(Stub.TRANSACTION_isSubActive, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_dial = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_call = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_showCallScreen = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_showCallScreenWithDialpad = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_endCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_answerRingingCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_silenceRinger = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_isOffhook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_isRinging = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_isIdle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_isRadioOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_isSimPinEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_cancelMissedCallsNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_supplyPin = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_supplyPuk = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_supplyPinReportResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_supplyPukReportResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_handlePinMmi = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_toggleRadioOnOff = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_setRadio = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_updateServiceLocation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_enableApnType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_disableApnType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_enableDataConnectivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_disableDataConnectivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_isDataConnectivityPossible = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_getCallState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_getDataActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_getDataState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_getActivePhoneType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_getCdmaEriIconIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_getCdmaEriIconMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_getCdmaEriText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_needsOtaServiceProvisioning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
static final int TRANSACTION_getVoiceMessageCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
static final int TRANSACTION_getNetworkType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
static final int TRANSACTION_hasIccCard = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
static final int TRANSACTION_getLteOnCdmaMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
static final int TRANSACTION_getIccPin1RetryCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 38);
static final int TRANSACTION_getAllCellInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 39);
static final int TRANSACTION_getDefaultSubscription = (android.os.IBinder.FIRST_CALL_TRANSACTION + 40);
static final int TRANSACTION_getPreferredVoiceSubscription = (android.os.IBinder.FIRST_CALL_TRANSACTION + 41);
static final int TRANSACTION_getPreferredDataSubscription = (android.os.IBinder.FIRST_CALL_TRANSACTION + 42);
static final int TRANSACTION_setPreferredDataSubscription = (android.os.IBinder.FIRST_CALL_TRANSACTION + 43);
static final int TRANSACTION_isSimPukLocked = (android.os.IBinder.FIRST_CALL_TRANSACTION + 44);
static final int TRANSACTION_isSubActive = (android.os.IBinder.FIRST_CALL_TRANSACTION + 45);
}
/**
     * Dial a number. This doesn't place the call. It displays
     * the Dialer screen for that subscription.
     * @param number the number to be dialed. If null, this
     * would display the Dialer screen with no number pre-filled.
     * @param subscription user preferred subscription.
     */
public void dial(java.lang.String number, int subscription) throws android.os.RemoteException;
/**
     * Place a call to the specified number on particular subscription.
     * @param number the number to be called.
     * @param subscription user preferred subscription.
     */
public void call(java.lang.String number, int subscription) throws android.os.RemoteException;
/**
     * If there is currently a call in progress, show the call screen.
     * The DTMF dialpad may or may not be visible initially, depending on
     * whether it was up when the user last exited the InCallScreen.
     *
     * @return true if the call screen was shown.
     */
public boolean showCallScreen() throws android.os.RemoteException;
/**
     * Variation of showCallScreen() that also specifies whether the
     * DTMF dialpad should be initially visible when the InCallScreen
     * comes up.
     *
     * @param showDialpad if true, make the dialpad visible initially,
     *                    otherwise hide the dialpad initially.
     * @return true if the call screen was shown.
     *
     * @see showCallScreen
     */
public boolean showCallScreenWithDialpad(boolean showDialpad) throws android.os.RemoteException;
/**
     * End call on particular subscription or go to the Home screen
     * @param subscription user preferred subscription.
     * @return whether it hung up
     */
public boolean endCall(int subscription) throws android.os.RemoteException;
/**
     * Answer the currently-ringing call on particular subscription.
     *
     * If there's already a current active call, that call will be
     * automatically put on hold.  If both lines are currently in use, the
     * current active call will be ended.
     *
     * TODO: provide a flag to let the caller specify what policy to use
     * if both lines are in use.  (The current behavior is hardwired to
     * "answer incoming, end ongoing", which is how the CALL button
     * is specced to behave.)
     *
     * TODO: this should be a oneway call (especially since it's called
     * directly from the key queue thread).
     *
     * @param subscription user preferred subscription.
     */
public void answerRingingCall(int subscription) throws android.os.RemoteException;
/**
     * Silence the ringer if an incoming call is currently ringing.
     * (If vibrating, stop the vibrator also.)
     *
     * It's safe to call this if the ringer has already been silenced, or
     * even if there's no incoming call.  (If so, this method will do nothing.)
     *
     * TODO: this should be a oneway call too (see above).
     *       (Actually *all* the methods here that return void can
     *       probably be oneway.)
     */
public void silenceRinger() throws android.os.RemoteException;
/**
     * Check if a particular subscription has an active or holding call
     *
     * @param subscription user preferred subscription.
     * @return true if the phone state is OFFHOOK.
     */
public boolean isOffhook(int subscription) throws android.os.RemoteException;
/**
     * Check if an incoming phone call is ringing or call waiting
     * on a particular subscription.
     *
     * @param subscription user preferred subscription.
     * @return true if the phone state is RINGING.
     */
public boolean isRinging(int subscription) throws android.os.RemoteException;
/**
     * Check if the phone is idle on a particular subscription.
     *
     * @param subscription user preferred subscription.
     * @return true if the phone state is IDLE.
     */
public boolean isIdle(int subscription) throws android.os.RemoteException;
/**
     * Check to see if the radio is on or not on particular subscription.
     * @param subscription user preferred subscription.
     * @return returns true if the radio is on.
     */
public boolean isRadioOn(int subscription) throws android.os.RemoteException;
/**
     * Check if the SIM pin lock is enable
     * for particular subscription.
     * @param subscription user preferred subscription.
     * @return true if the SIM pin lock is enabled.
     */
public boolean isSimPinEnabled(int subscription) throws android.os.RemoteException;
/**
     * Cancels the missed calls notification on particular subscription.
     * @param subscription user preferred subscription.
     */
public void cancelMissedCallsNotification(int subscription) throws android.os.RemoteException;
/**
     * Supply a pin to unlock the SIM for particular subscription.
     * Blocks until a result is determined.
     * @param pin The pin to check.
     * @param subscription user preferred subscription.
     * @return whether the operation was a success.
     */
public boolean supplyPin(java.lang.String pin, int subscription) throws android.os.RemoteException;
/**
     * Supply puk to unlock the SIM and set SIM pin to new pin.
     *  Blocks until a result is determined.
     * @param puk The puk to check.
     *        pin The new pin to be set in SIM
     * @param subscription user preferred subscription.
     * @return whether the operation was a success.
     */
public boolean supplyPuk(java.lang.String puk, java.lang.String pin, int subscription) throws android.os.RemoteException;
/**
     * Supply a pin to unlock the SIM.  Blocks until a result is determined.
     * Returns a specific success/error code.
     * @param pin The pin to check.
     * @param subscription user preferred subscription.
     * @return Phone.PIN_RESULT_SUCCESS on success. Otherwise error code
     */
public int supplyPinReportResult(java.lang.String pin, int subscription) throws android.os.RemoteException;
/**
     * Supply puk to unlock the SIM and set SIM pin to new pin.
     * Blocks until a result is determined.
     * Returns a specific success/error code.
     * @param puk The puk to check
     *        pin The pin to check.
     * @param subscription user preferred subscription.
     * @return Phone.PIN_RESULT_SUCCESS on success. Otherwise error code
     */
public int supplyPukReportResult(java.lang.String puk, java.lang.String pin, int subscription) throws android.os.RemoteException;
/**
     * Handles PIN MMI commands (PIN/PIN2/PUK/PUK2), which are initiated
     * without SEND (so <code>dial</code> is not appropriate) for
     * a particular subscription.
     * @param dialString the MMI command to be executed.
     * @param subscription user preferred subscription.
     * @return true if MMI command is executed.
     */
public boolean handlePinMmi(java.lang.String dialString, int subscription) throws android.os.RemoteException;
/**
     * Toggles the radio on or off on particular subscription.
     * @param subscription user preferred subscription.
     */
public void toggleRadioOnOff(int subscription) throws android.os.RemoteException;
/**
     * Set the radio to on or off on particular subscription.
     * @param subscription user preferred subscription.
     */
public boolean setRadio(boolean turnOn, int subscription) throws android.os.RemoteException;
/**
     * Request to update location information for a subscrition in service state
     * @param subscription user preferred subscription.
     */
public void updateServiceLocation(int subscription) throws android.os.RemoteException;
/**
     * Enable a specific APN type.
     */
public int enableApnType(java.lang.String type) throws android.os.RemoteException;
/**
     * Disable a specific APN type.
     */
public int disableApnType(java.lang.String type) throws android.os.RemoteException;
/**
     * Allow mobile data connections.
     */
public boolean enableDataConnectivity() throws android.os.RemoteException;
/**
     * Disallow mobile data connections.
     */
public boolean disableDataConnectivity() throws android.os.RemoteException;
/**
     * Report whether data connectivity is possible.
     */
public boolean isDataConnectivityPossible() throws android.os.RemoteException;
/**
     * Returns the call state for a subscription.
     */
public int getCallState(int subscription) throws android.os.RemoteException;
public int getDataActivity() throws android.os.RemoteException;
public int getDataState() throws android.os.RemoteException;
/**
     * Returns the current active phone type as integer for particular subscription.
     * Returns TelephonyManager.PHONE_TYPE_CDMA if RILConstants.CDMA_PHONE
     * and TelephonyManager.PHONE_TYPE_GSM if RILConstants.GSM_PHONE
     * @param subscription user preferred subscription.
     */
public int getActivePhoneType(int subscription) throws android.os.RemoteException;
/**
     * Returns the CDMA ERI icon index to display on particular subscription.
     * @param subscription user preferred subscription.
     */
public int getCdmaEriIconIndex(int subscription) throws android.os.RemoteException;
/**
     * Returns the CDMA ERI icon mode on particular subscription,
     * 0 - ON
     * 1 - FLASHING
     * @param subscription user preferred subscription.
     */
public int getCdmaEriIconMode(int subscription) throws android.os.RemoteException;
/**
     * Returns the CDMA ERI text for particular subscription,
     * @param subscription user preferred subscription.
     */
public java.lang.String getCdmaEriText(int subscription) throws android.os.RemoteException;
/**
     * Returns true if OTA service provisioning needs to run.
     * Only relevant on some technologies, others will always
     * return false.
     */
public boolean needsOtaServiceProvisioning() throws android.os.RemoteException;
/**
     * Returns the unread count of voicemails for a subscription.
     * @param subscription user preferred subscription.
     * Returns the unread count of voicemails
     */
public int getVoiceMessageCount(int subscription) throws android.os.RemoteException;
/**
     * Returns the network type of a subscription.
     * @param subscription user preferred subscription.
     * Returns the network type
     */
public int getNetworkType(int subscription) throws android.os.RemoteException;
/**
     * Return true if an ICC card is present for a subscription.
     * @param subscription user preferred subscription.
     * Return true if an ICC card is present
     */
public boolean hasIccCard(int subscription) throws android.os.RemoteException;
/**
     * Return if the current radio is LTE on CDMA. This
     * is a tri-state return value as for a period of time
     * the mode may be unknown.
     *
     * @return {@link Phone#LTE_ON_CDMA_UNKNOWN}, {@link Phone#LTE_ON_CDMA_FALSE}
     * or {@link PHone#LTE_ON_CDMA_TRUE}
     */
public int getLteOnCdmaMode(int subscription) throws android.os.RemoteException;
/**
     * Gets the number of attempts remaining for PIN1/PUK1 unlock
     * for a subscription.
     * @param subscription user preferred subscription.
     * Gets the number of attempts remaining for PIN1/PUK1 unlock.
     */
public int getIccPin1RetryCount(int subscription) throws android.os.RemoteException;
/**
     * Returns the all observed cell information of the device.
     */
public java.util.List<android.telephony.CellInfo> getAllCellInfo() throws android.os.RemoteException;
/**
     * get default subscription
     * @return subscription id
     */
public int getDefaultSubscription() throws android.os.RemoteException;
/**
     * get user prefered voice subscription
     * @return subscription id
     */
public int getPreferredVoiceSubscription() throws android.os.RemoteException;
/**
     * get user prefered data subscription
     * @return subscription id
     */
public int getPreferredDataSubscription() throws android.os.RemoteException;
/*
     * Set user prefered data subscription
     * @return true if success
     */
public boolean setPreferredDataSubscription(int subscription) throws android.os.RemoteException;
public boolean isSimPukLocked(int subscription) throws android.os.RemoteException;
/*
     * Get subscription is activated or not
     * @return true if subscription is activated
     */
public boolean isSubActive(int subscription) throws android.os.RemoteException;
}
