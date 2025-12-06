import React from 'react';
import { Check, X } from 'lucide-react';

const PasswordStrengthIndicator = ({ password }) => {
    const requirements = [
        { label: 'At least 8 characters', test: (pwd) => pwd.length >= 8 },
        { label: 'One uppercase letter', test: (pwd) => /[A-Z]/.test(pwd) },
        { label: 'One lowercase letter', test: (pwd) => /[a-z]/.test(pwd) },
        { label: 'One number', test: (pwd) => /\d/.test(pwd) },
        { label: 'One special character (@$!%*?&)', test: (pwd) => /[@$!%*?&]/.test(pwd) }
    ];

    const metRequirements = requirements.filter(req => req.test(password));
    const strength = metRequirements.length;

    const getStrengthColor = () => {
        if (strength <= 2) return 'bg-red-500';
        if (strength <= 3) return 'bg-yellow-500';
        if (strength <= 4) return 'bg-blue-500';
        return 'bg-green-500';
    };

    const getStrengthText = () => {
        if (strength <= 2) return 'Weak';
        if (strength <= 3) return 'Fair';
        if (strength <= 4) return 'Good';
        return 'Strong';
    };

    const getStrengthTextColor = () => {
        if (strength <= 2) return 'text-red-600';
        if (strength <= 3) return 'text-yellow-600';
        if (strength <= 4) return 'text-blue-600';
        return 'text-green-600';
    };

    if (!password) return null;

    return (
        <div className="mt-2 space-y-2">
            {/* Strength Bar */}
            <div>
                <div className="flex justify-between items-center mb-1">
                    <span className="text-xs font-medium text-gray-700">Password Strength</span>
                    <span className={`text-xs font-semibold ${getStrengthTextColor()}`}>
                        {getStrengthText()}
                    </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                    <div
                        className={`h-2 rounded-full transition-all duration-300 ${getStrengthColor()}`}
                        style={{ width: `${(strength / requirements.length) * 100}%` }}
                    />
                </div>
            </div>

            {/* Requirements Checklist */}
            <div className="space-y-1">
                {requirements.map((req, index) => {
                    const isMet = req.test(password);
                    return (
                        <div key={index} className="flex items-center gap-2">
                            {isMet ? (
                                <Check className="h-4 w-4 text-green-500 flex-shrink-0" />
                            ) : (
                                <X className="h-4 w-4 text-gray-300 flex-shrink-0" />
                            )}
                            <span className={`text-xs ${isMet ? 'text-green-700' : 'text-gray-500'}`}>
                                {req.label}
                            </span>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default PasswordStrengthIndicator;
