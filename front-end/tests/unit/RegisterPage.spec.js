import { mount, createLocalVue } from '@vue/test-utils';
import RegisterPage from '@/views/RegisterPage';
import VueRouter from 'vue-router';
import { Vuelidate } from 'vuelidate';
import registrationService from '@/services/registration';

// vm.$router에 접속할 수 있도록
// 테스트에 Vue Router 추가하기
const localVue = createLocalVue();
localVue.use(VueRouter);
localVue.use(Vuelidate);
const router = new VueRouter();

// registrationService의 목
jest.mock('@/services/registration');

describe('RegisterPage.vue', () => {
    let wrapper;
    let fieldUsername;
    let fieldEmailAddress;
    let fieldPassword;
    let buttonSubmit;
    let registerSpy;

    beforeEach(() => {
        wrapper = mount(RegisterPage, {
            localVue,
            router,
        });
        fieldUsername = wrapper.find('#username');
        fieldEmailAddress = wrapper.find('#emailAddress');
        fieldPassword = wrapper.find('#password');
        buttonSubmit = wrapper.find('form button[type="submit"]');
        registerSpy = jest.spyOn(registrationService, 'register');
    });

    afterEach(() => {
        registerSpy.mockReset();
        registerSpy.mockRestore();
    });

    afterAll(() => {
        jest.restoreAllMocks();
    });

    it('should render correct contents', () => {
        expect(wrapper.find('.logo').attributes().src).toEqual(
            '/images/logo.png'
        );
        expect(wrapper.find('.tagline').text()).toEqual(
            'Open source task management tool'
        );
        expect(fieldUsername.element.value).toEqual('');
        expect(fieldEmailAddress.element.value).toEqual('');
        expect(fieldPassword.element.value).toEqual('');
        expect(buttonSubmit.text()).toEqual('Create account');
    });

    it('should contain data model with initial values', () => {
        expect(wrapper.vm.form.username).toEqual('');
        expect(wrapper.vm.form.emailAddress).toEqual('');
        expect(wrapper.vm.form.password).toEqual('');
    });

    it('should have form inputs bound with data model', async () => {
        const username = 'sunny';
        const emailAddress = 'sunny@local';
        const password = 'VueJsRocks!';

        await wrapper.setData({
            form: {
                username: username,
                emailAddress: emailAddress,
                password: password,
            },
        });

        expect(fieldUsername.element.value).toEqual(username);
        expect(fieldEmailAddress.element.value).toEqual(emailAddress);
        expect(fieldPassword.element.value).toEqual(password);
    });

    it('should have form submit event handler `submitForm`', async () => {
        const spyFn = jest.spyOn(wrapper.vm, 'submitForm');

        await buttonSubmit.trigger('submit');
        expect(spyFn).toBeCalled();
    });

    it('should register when it is a new user', async () => {
        expect.assertions(2)
        const stub = jest.fn();
        wrapper.vm.$router.push = stub;
        await wrapper.setData({
            form: {
                username: 'sunny',
                emailAddress: 'sunny@taskagile.com',
                password: 'JestRocks!',
            },
        });

        wrapper.vm.submitForm();

        // await buttonSubmit.trigger('submit')

        expect(registerSpy).toBeCalled();
        await wrapper.vm.$nextTick();
        expect(stub).toHaveBeenCalledWith({
            name: 'LoginPage',
        });
    });

    it('should fail it is not a new user', async () => {
        expect.assertions(3)
        await wrapper.setData({
            form: {
                username: 'ted',
                emailAddress: 'ted@taskagile.com',
                password: 'JestRocks!',
            },
        });
        expect(wrapper.find('.failed').isVisible()).toBe(false);
        await buttonSubmit.trigger('submit');
        expect(registerSpy).toBeCalled();
        await wrapper.vm.$nextTick();
        expect(wrapper.find('.failed').isVisible()).toBe(true);
    });

    it('should fail when email address is invalid', async () => {
        await wrapper.setData({
            form: {
                username: 'test',
                emailAddress: 'bad-email-address',
                password: 'JestRocks!'
            },
        });
        // wrapper.vm.form.emailAddress = 'bad-email-address'

        wrapper.vm.submitForm();
        expect(registerSpy).not.toHaveBeenCalled();
    });

    it('should fail when username is invalid', async () => {
        await wrapper.setData({
            form: {
                username: 'a',
                emailAddress: 'test@taskagile.com',
                password: 'JestRocks!'
            },
        });
        // wrapper.vm.form.emailAddress = 'bad-email-address'

        wrapper.vm.submitForm();
        expect(registerSpy).not.toHaveBeenCalled();
    });

    it('should fail when password is invalid', async () => {
        await wrapper.setData({
            form: {
                username: 'test',
                emailAddress: 'test@taskagile.com',
                password: 'bad!'
            },
        });
        // wrapper.vm.form.emailAddress = 'bad-email-address'

        wrapper.vm.submitForm();
        expect(registerSpy).not.toHaveBeenCalled();
    });
});
