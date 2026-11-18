package com.sumpositive.onboarding.service;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sumpositive.onboarding.entity.ChecklistItem;
import com.sumpositive.onboarding.entity.OnboardingStep;
import com.sumpositive.onboarding.entity.User;
import com.sumpositive.onboarding.repository.ChecklistItemRepository;
import com.sumpositive.onboarding.repository.OnboardingStepRepository;
import com.sumpositive.onboarding.repository.UserRepository;

/**
 * SumBot brain: answers questions using the logged-in employee's
 * live data from the database.
 */
@Service
public class ChatbotService {

    private static final Pattern GREETING =
            Pattern.compile("\\b(hi|hello|hey)\\b");

    private final UserRepository userRepository;
    private final OnboardingStepRepository stepRepository;
    private final ChecklistItemRepository checklistRepository;

    public ChatbotService(
            UserRepository userRepository,
            OnboardingStepRepository stepRepository,
            ChecklistItemRepository checklistRepository) {
        this.userRepository = userRepository;
        this.stepRepository = stepRepository;
        this.checklistRepository = checklistRepository;
    }

    @Transactional(readOnly = true)
    public String reply(String email, String message) {

        // The user comes from the login token, never from the request body
        User user = userRepository.findByMail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        String q = message == null ? "" : message.toLowerCase().trim();

        List<OnboardingStep> steps = stepRepository.findByUserId(user.getId())
                .stream()
                .sorted(Comparator.comparing(OnboardingStep::getStepType))
                .toList();

        List<ChecklistItem> checklist =
                checklistRepository.findByUserId(user.getId())
                .stream()
                .sorted(Comparator.comparing(ChecklistItem::getItemType))
                .toList();

        boolean profileFilled = user.getProfile() != null;

        if (q.contains("checklist")) {
            return checklistReply(checklist);
        }

        if (q.contains("profile")) {
            return profileReply(profileFilled);
        }

        if (q.contains("policy") || q.contains("policies")
                || q.contains("guideline")) {
            return "You can find company policies, workplace guidelines, "
                    + "IT guidelines and reporting protocols in the "
                    + "Onboarding Steps section.";
        }

        if (q.contains("next") || q.contains("pending")
                || q.contains("left") || q.contains("remaining")
                || q.contains("todo") || q.contains("to do")) {
            return pendingReply(steps);
        }

        if (q.contains("status") || q.contains("progress")
                || q.contains("complete")) {
            return statusReply(steps, checklist);
        }

        if (GREETING.matcher(q).find()) {
            String name = user.getProfile() != null
                    ? user.getProfile().getFirstName()
                    : null;

            return "Hello" + (name != null ? " " + name : "")
                    + "! \uD83D\uDE0A I can tell you your onboarding "
                    + "status, pending steps, checklist and profile "
                    + "details. What would you like to know?";
        }

        return "I can help with your onboarding status, pending steps, "
                + "joining checklist, profile and company policies. "
                + "Try asking: \"What is my onboarding status?\"";
    }

    private String statusReply(
            List<OnboardingStep> steps,
            List<ChecklistItem> checklist) {

        if (steps.isEmpty()) {
            return "Your onboarding has not started yet. Open the "
                    + "Onboarding Steps page to begin.";
        }

        long done = steps.stream().filter(OnboardingStep::isCompleted).count();
        int total = steps.size();
        int percent = (int) ((done * 100) / total);

        if (done == total) {
            return "\uD83C\uDF89 Your onboarding is complete! All "
                    + total + " steps are done.";
        }

        String reply = "Your onboarding is " + percent + "% complete ("
                + done + " of " + total + " steps done). Pending: "
                + pendingNames(steps) + ".";

        if (!checklist.isEmpty()) {
            long checkDone = checklist.stream()
                    .filter(ChecklistItem::isCompleted).count();
            reply += " Checklist: " + checkDone + " of "
                    + checklist.size() + " items done.";
        }

        return reply;
    }

    private String pendingReply(List<OnboardingStep> steps) {

        if (steps.isEmpty()) {
            return "Your onboarding has not started yet. Open the "
                    + "Onboarding Steps page to begin.";
        }

        List<OnboardingStep> pending = steps.stream()
                .filter(s -> !s.isCompleted())
                .toList();

        if (pending.isEmpty()) {
            return "Nothing is pending. You have completed every "
                    + "onboarding step! \uD83C\uDF89";
        }

        return "You have " + pending.size() + " step(s) left: "
                + pendingNames(steps) + ". Your next step is "
                + label(pending.get(0).getStepType().name()) + ".";
    }

    private String checklistReply(List<ChecklistItem> checklist) {

        if (checklist.isEmpty()) {
            return "You have not opened your joining checklist yet. "
                    + "Open the Checklist page to start: document "
                    + "submission, equipment collection and policy "
                    + "acknowledgement.";
        }

        long done = checklist.stream()
                .filter(ChecklistItem::isCompleted).count();

        String items = checklist.stream()
                .map(i -> label(i.getItemType().name())
                        + (i.isCompleted() ? " (done)" : " (pending)"))
                .collect(Collectors.joining(", "));

        if (done == checklist.size()) {
            return "Your joining checklist is complete: " + items + ".";
        }

        return "Checklist: " + done + " of " + checklist.size()
                + " done. " + items + ". Open the Checklist page to "
                + "finish the pending items.";
    }

    private String profileReply(boolean profileFilled) {

        if (profileFilled) {
            return "Your employee profile is already submitted. "
                    + "You can review it from the Profile section.";
        }

        return "Your profile is not submitted yet. Open the Profile "
                + "section from your Dashboard and fill in the "
                + "required information.";
    }

    private String pendingNames(List<OnboardingStep> steps) {
        return steps.stream()
                .filter(s -> !s.isCompleted())
                .map(s -> label(s.getStepType().name()))
                .collect(Collectors.joining(", "));
    }

    // "JOINING_CHECKLIST" -> "Joining Checklist"
    private String label(String enumName) {

        String[] words = enumName.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1));
        }

        return result.toString();
    }
}