"use client";
import React, { useState } from 'react';
import Link from "next/link";
import axios, { AxiosResponse } from 'axios';
import { FieldValues, set, useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { useRouter } from 'next/navigation';

const Page = () => {
    const [step, setStep] = useState(1);
    const [showPassword, setShowPassword] = useState(false);
    const [userId, setUserId] = useState<number | null>(null);
    const [certificate, setCertificate] = useState<File | null>(null);
    const [profilePhoto, setProfilePhoto] = useState<File | null>(null);
    const [formData, setFormData] = useState({
        role: ''
    });

    const { register, handleSubmit, formState: { errors } } = useForm();
    const router = useRouter();

    interface firstStep {
        firstName: string,
        lastName: string,
        userName: string,
        email: string,
        password: string,
        role: string
    }

    interface secondStep {
        bio: string,
        proficiencies: string[]
    }

    const onPictureChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files.length > 0) {
            if (step === 2) {
                setCertificate(e.target.files[0]);
            } else if (step === 3) {
                setProfilePhoto(e.target.files[0]);
            }
        }
    };

    const onSubmit = async (data: FieldValues) => {
        console.log('data:', data);
        const base_url = "http://localhost:8080";
        if (step === 1) {
            const { firstName, lastName, userName, email, password, role } = data as firstStep;

            setFormData({
                ...formData,
                role: role
            });

            const response = await axios.post(`${base_url}/auth/register`, {
                firstName,
                lastName,
                userName,
                email,
                password,
                role
            });

            if (response && response.data.success) {
                setUserId(response.data.userId);
                toast.success(response.data.message);
                setStep(2);
            } else {
                toast.error(response?.data?.message || 'An error occurred');
            }
        } else if (step === 2) {
            const { bio, proficiencies } = data as secondStep;
            if (!certificate) {
                toast.error('Please upload a certificate file.');
                return;
            }

            const obj: any = {
                userId: userId,
                bio: bio,
                proficiencies: proficiencies
            }

            console.log('obj:', obj);
            console.log(`{"userId": ${userId}, "bio": "${bio}", "proficiencies": [${JSON.stringify(proficiencies)}]}`);

            const formData = new FormData();
            formData.append('certificate', certificate);
            formData.append(
                'doctorRegistrationBody',
                `{"userId": ${userId}, "bio": "${bio}", "proficiencies": [${JSON.stringify(proficiencies)}]}`
            );

            try {
                const response = await axios.post(`${base_url}/auth/register/doctor`, formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });
                if (response.status === 200) {
                    setUserId(response.data.userId);
                    toast.success('Doctor registered successfully!');
                    setStep(3);
                }
            } catch (error) {
                console.error(error);
                toast.error('An error occurred while submitting the form.');
            }
        }
        else if (step === 3) {
            const formData = new FormData();
            if (!profilePhoto) {
                toast.error('Please upload a Profile Photo file.');
                return;
            }
            formData.append('profilePhoto', profilePhoto);
            try {
                const response = await axios.post(`${base_url}/auth/upload-profile-photo/${userId}`, formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });

                if (response && response.status === 200) {
                    toast.success("Profile photo uploaded successfully!");
                    router.replace('/login');
                } else {
                    console.error(response);
                    toast.error('An error occurred');
                }
            } catch (error) {
                console.error(error);
                toast.error('An error occurred while submitting the form.');
            }
        }
    };


    const prevStep = () => {
        if (step === 3 && formData.role === 'USER') {
            setStep(1);
        } else {
            setStep((prev) => prev - 1);
        }
    };

    return (
        <div className="min-h-screen flex justify-center align-center bg-gradient-to-br from-blue-50 via-white to-blue-50 pt-16">
            <div className="container mx-auto py-6 flex items-center justify-center px-4">
                <div className="w-full max-w-6xl grid lg:grid-cols-5 gap-[10%] items-start">
                    {/* Left Content */}
                    <div className="lg:col-span-2 hidden lg:block sticky top-24">
                        <div className="space-y-6">
                            <div>
                                <h1 className="text-4xl font-bold text-blue-950 mb-3">
                                    Your Health Journey Starts Here
                                </h1>
                                <p className="text-lg text-gray-600">
                                    Join our trusted healthcare platform and connect with top medical professionals.
                                </p>
                            </div>

                            <div className="grid grid-cols-1 gap-3">
                                <div className="bg-white/70 backdrop-blur-sm p-5 rounded-xl border border-blue-100">
                                    <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center mb-3">
                                        <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                                        </svg>
                                    </div>
                                    <h3 className="text-lg font-semibold text-blue-950 mb-1">Verified Professionals</h3>
                                    <p className="text-sm text-gray-600">Connect with certified healthcare providers you can trust.</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Right Content - Form */}
                    <div className="lg:col-span-3 w-full">
                        <div className="bg-white rounded-xl shadow-lg p-6">
                            <div className="flex items-center justify-between mb-6">
                                <div>
                                    <h2 className="text-2xl font-bold text-blue-950">Create Account</h2>
                                    <p className="text-gray-500 mt-1">Step {step} of 3</p>
                                </div>
                                <div className="flex gap-2">
                                    {[1, 2, 3].map((s) => (
                                        <div
                                            key={s}
                                            className={`w-2 h-2 rounded-full transition-colors ${step >= s ? 'bg-blue-500' : 'bg-gray-200'
                                                }`}
                                        />
                                    ))}
                                </div>
                            </div>

                            {step === 1 && (
                                <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
                                    <div className="grid grid-cols-2 gap-3">
                                        <div>
                                            <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 mb-1">
                                                First Name
                                            </label>
                                            <input
                                                type="text"
                                                id="firstName"
                                                className="w-full px-3 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-transparent"

                                                {
                                                ...register('firstName', {
                                                    required: 'First Name is required'
                                                })
                                                }
                                            />
                                            {
                                                errors.firstName && <p className="text-red-500 text-sm">{String(errors.firstName.message)}</p>
                                            }
                                        </div>
                                        <div>
                                            <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 mb-1">
                                                Last Name
                                            </label>
                                            <input
                                                type="text"
                                                id="lastName"
                                                className="w-full px-3 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                                required
                                                {
                                                ...register('lastName', {
                                                    required: 'Last Name is required'
                                                })
                                                }
                                            />
                                            {
                                                errors.lastName && <p className="text-red-500 text-sm">{String(errors.lastName.message)}</p>
                                            }
                                        </div>
                                    </div>

                                    <div className="grid grid-cols-2 gap-3">
                                        <div>
                                            <label htmlFor="userName" className="block text-sm font-medium text-gray-700 mb-1">
                                                Username
                                            </label>
                                            <input
                                                type="text"
                                                id="userName"
                                                className="w-full px-3 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                                required
                                                {
                                                ...register('userName', {
                                                    required: 'Username is required'
                                                })
                                                }
                                            />
                                            {
                                                errors.userName && <p className="text-red-500 text-sm">{String(errors.userName.message)}</p>
                                            }
                                        </div>
                                        <div>
                                            <label htmlFor="role" className="block text-sm font-medium text-gray-700 mb-1">
                                                Role
                                            </label>
                                            <select
                                                id="role"
                                                className="w-full px-3 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                                required
                                                {
                                                ...register('role', {
                                                    required: 'Role is required'
                                                })
                                                }
                                            >
                                                <option value="">Select Role</option>
                                                <option value="DOCTOR">Doctor</option>
                                                <option value="USER">User</option>
                                            </select>
                                        </div>
                                    </div>

                                    <div>
                                        <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                                            Email Address
                                        </label>
                                        <input
                                            type="email"
                                            id="email"
                                            className="w-full px-3 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                            placeholder="you@example.com"
                                            required
                                            {
                                            ...register('email', {
                                                pattern: {
                                                    value: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/,
                                                    message: 'Invalid email address'
                                                }
                                            })
                                            }
                                        />
                                        {
                                            errors.email && <p className="text-red-500 text-sm">{String(errors.email.message)}</p>
                                        }
                                    </div>

                                    <div>
                                        <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
                                            Password
                                        </label>
                                        <div className="relative">
                                            <input
                                                type={showPassword ? "text" : "password"}
                                                id="password"
                                                className="w-full px-3 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                                required
                                                {
                                                ...register('password', {
                                                    required: 'Password is required',
                                                    minLength: {
                                                        value: 8,
                                                        message: 'Password must be at least 8 characters'
                                                    },
                                                    pattern: {
                                                        value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/,
                                                        message: 'Password must contain at least one uppercase letter, one lowercase letter, and one number'
                                                    }
                                                })
                                                }
                                            />
                                            <button
                                                type="button"
                                                onClick={() => setShowPassword(!showPassword)}
                                                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                                            >
                                                {showPassword ? "Hide" : "Show"}
                                            </button>
                                        </div>
                                    </div>

                                    <button
                                        type="submit"
                                        className="w-full bg-blue-500 text-white py-2.5 px-4 rounded-lg hover:bg-blue-600 transition-colors font-medium mt-2"
                                    >
                                        Continue
                                    </button>
                                </form>
                            )}

                            {step === 2 && (
                                <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
                                    <div>
                                        <label htmlFor="certificate" className="block text-sm font-medium text-gray-700 mb-1">
                                            Upload Certificate
                                        </label>
                                        <input
                                            type="file"
                                            id="certificate"
                                            accept=".pdf,.jpg,.jpeg,.png"
                                            className="w-full px-3 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                            required
                                            onChange={onPictureChange}
                                        />
                                    </div>

                                    <div>
                                        <label htmlFor="bio" className="block text-sm font-medium text-gray-700 mb-1">
                                            Bio
                                        </label>
                                        <textarea
                                            id="bio"
                                            className="w-full px-3 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                            rows={4}
                                            required
                                            {
                                            ...register('bio', {
                                                required: 'Bio is required',
                                                maxLength: {
                                                    value: 500,
                                                    message: 'Bio must not exceed 500 characters'
                                                }
                                            })
                                            }
                                        />
                                    </div>

                                    <div>
                                        <label htmlFor="proficiencies" className="block text-sm font-medium text-gray-700 mb-1">
                                            Proficiencies (comma-separated)
                                        </label>
                                        <input
                                            type="text"
                                            id="proficiencies"
                                            // onChange={(e) => {
                                            //     const proficienciesArray = e.target.value.split(',').map(el => el.trim());
                                            //     setFormData({
                                            //         ...formData,
                                            //         proficiencies: proficienciesArray
                                            //     });
                                            // }}
                                            className="w-full px-3 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                            placeholder="e.g. Cardiology, Pediatrics, Surgery"
                                            required
                                            {
                                            ...register('proficiencies', {
                                                required: 'Proficiencies are required',
                                                validate: {
                                                    format: (value) => value.split(',').length > 0 || 'Proficiencies must be comma-separated'
                                                }
                                            })
                                            }
                                        />
                                    </div>

                                    <div className="flex gap-3">
                                        <button
                                            type="button"
                                            onClick={prevStep}
                                            className="w-full bg-gray-100 text-gray-700 py-2.5 px-4 rounded-lg hover:bg-gray-200 transition-colors font-medium"
                                        >
                                            Back
                                        </button>
                                        <button
                                            type="submit"
                                            className="w-full bg-blue-500 text-white py-2.5 px-4 rounded-lg hover:bg-blue-600 transition-colors font-medium"
                                        >
                                            Continue
                                        </button>
                                    </div>
                                </form>
                            )}

                            {step === 3 && (
                                <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
                                    <div>
                                        <label htmlFor="profilePhoto" className="block text-sm font-medium text-gray-700 mb-1">
                                            Profile Photo
                                        </label>
                                        <input
                                            type="file"
                                            id="profilePhoto"
                                            accept="image/*"
                                            className="w-full px-3 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                            required
                                            onChange={onPictureChange}
                                        />
                                    </div>

                                    <div className="flex gap-3">
                                        <button
                                            type="button"
                                            onClick={prevStep}
                                            className="w-full bg-gray-100 text-gray-700 py-2.5 px-4 rounded-lg hover:bg-gray-200 transition-colors font-medium"
                                        >
                                            Back
                                        </button>
                                        <button
                                            type="submit"
                                            className="w-full bg-blue-500 text-white py-2.5 px-4 rounded-lg hover:bg-blue-600 transition-colors font-medium"
                                        >
                                            Complete Registration
                                        </button>
                                    </div>
                                </form>
                            )}

                            <div className="mt-6 pt-4 border-t border-gray-100">
                                <p className="text-center text-gray-600 mb-3">Already have an account?</p>
                                <Link
                                    href="/login"
                                    className="block w-full text-center px-4 py-2 rounded-lg border border-blue-100 text-blue-600 font-medium hover:bg-blue-50 transition-colors"
                                >
                                    Sign In
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );


};
export default Page;